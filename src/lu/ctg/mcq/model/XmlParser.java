package lu.ctg.mcq.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * @author Johann Bernez
 */
public class XmlParser {

	// 
    private static final String ns = null;
    
    public List<Question> parse(InputStream...in) throws XmlPullParserException, IOException {
    	List<Question> questions = new ArrayList<>();
    	for (InputStream i : in) {
	        try {
	        	XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new InputStreamReader(i, "UTF8"));
	            parser.next();
	            questions.addAll(readQuestions(parser));
	        } finally {
	            i.close();
	        }
    	}
    	return questions;
    }
	
	private List<Question> readQuestions(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<Question> entries = new ArrayList<>();

	    parser.require(XmlPullParser.START_TAG, ns, "questions");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("question")) {
	            entries.add(readQuestion(parser));
	        } else {
	            skip(parser);
	        }
	    }
	    
	    return entries;
	}
	
	private Question readQuestion(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "question");
	    String title = null;
	    String explanation = null;
	    String group = null;
	    List<Option> options = null;
	    List<Answer> answers = null;
	    
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("title")) {
	            title = readTitle(parser);
	        } else if (name.equals("options")) {
	            options = readOptions(parser);
	        } else if (name.equals("answers")) {
	            answers = readAnswers(parser);
	        } else if (name.equals("explanation")) {
	            explanation = readExplanation(parser);
	        } else if (name.equals("group")) {
	        	group = readGroup(parser);
	        } else {
	            skip(parser);
	        }
	    }
	    Question q = new Question(title, explanation, group);
	    q.setOptions(options);
	    q.setAnswers(answers);
	    return q;
	}
	
	private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "title");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "title");
	    return title;
	}
	
	private List<Option> readOptions(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<Option> options = new ArrayList<>();

	    parser.require(XmlPullParser.START_TAG, ns, "options");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("option")) {
	        	options.add(readOption(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return options;
	}
	
	private Option readOption(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "option");
	    String sValue = parser.getAttributeValue(null, "value");
	    int value = "".equals(sValue) ? 0 : Integer.parseInt(sValue);  
        String textualValue = parser.getAttributeValue(null, "textualValue");
        String text = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "option");
	    return new Option(value, textualValue, text);
	}
	
	private List<Answer> readAnswers(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<Answer> options = new ArrayList<>();

	    parser.require(XmlPullParser.START_TAG, ns, "answers");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("answer")) {
	        	options.add(readAnswer(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return options;
	}
	
	private Answer readAnswer(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "answer");
	    String sValue = parser.getAttributeValue(null, "value");
	    int value = "".equals(sValue) ? 0 : Integer.parseInt(sValue);
	    parser.nextTag();
	    parser.require(XmlPullParser.END_TAG, ns, "answer");
	    return new Answer(value);
	}
	
	private String readExplanation(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "explanation");
	    String explanation = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "explanation");
	    return explanation;
	}
	
	private String readGroup(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "group");
	    String explanation = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "group");
	    return explanation;
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        try {
	        	parser.nextTag();
	        } catch (XmlPullParserException e) {
	        	throw e;
	        }
	    }
	    return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
