package sample;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;

public class sentimentAnalyzer {
    int veryPositive = 0;
    int positive = 0;
    int neutral = 0;
    int negative = 0;
    int veryNegative = 0;

    public sentimentAnalyzer(String text){

        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();

        CoreDocument coreDocument = new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);

        List<CoreSentence> sentences = coreDocument.sentences();

        for(CoreSentence sentence : sentences) {
            String sentiment = sentence.sentiment();

            if (sentiment.equals("Very Positive")){
                setVeryPositive();
            }
            else if (sentiment.equals("Positive")){
                setPositive();
            }
            else if (sentiment.equals("Neutral")){
                setNeutral();
            }
            else if (sentiment.equals("Negative")){
                setNegative();
            }
            else if(sentiment.equals("Very Negative")){
                setVeryNegative();
            }
            /*
            else{
                System.out.println("Error");
                break;
            }*/
            //System.out.println(sentiment + "\t" + sentence);
        }
    }

    public void setVeryPositive(){
        veryPositive ++;
    }
    public void setPositive(){
        positive ++;
    }
    public void setNeutral(){
        neutral ++;
    }
    public void setNegative(){
        negative ++;
    }
    public void setVeryNegative(){
        veryNegative ++;
    }
    public int getVeryPositive(){
        return veryPositive;
    }
    public int getPositive(){
        return positive;
    }
    public int getNeutral(){
        return neutral;
    }
    public int getNegative(){
        return negative;
    }
    public int getVeryNegative(){
        return veryNegative;
    }
}
