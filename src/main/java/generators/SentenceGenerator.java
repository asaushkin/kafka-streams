package generators;

/*
  The last verse of a well-known nursery rhyme:

    This is the farmer sowing his corn
    That kept the rooster that crowed in the morn
    That waked the judge all shaven and shorn
    That married the man all tattered and torn
    That kissed the maiden all forlorn
    That milked the cow with the crumpled horn
    That tossed the dog
    That worried the cat
    That chased the rat
    That ate the cheese
    That lay in the house that Jack built.

  Some rules that capture the syntax of this verse:

    <sentence> ::= <simple_sentence> [ and <sentence> ]

    <simple_sentence> ::=  this is [ <noun_phrase> ] the house that Jack built

    <noun_phrase> ::= the <noun> [ <modifier> ] that <verb> [ <noun_phrase> ]

    <noun> ::= farmer | rooster | judge | man | maiden | cow | dog | cat | cheese

    <verb> ::= kept | waked | married | milked | tossed | chased | lay in

    <modifier> ::= that crowed in the morn | all shaven and shorn |
                    all tattered and torn | all forlorn | with the crumpled horn

  This program implements these rules to generate random sentences.  All the
  verses of the rhyme can be generated, plus a lot of sentences that make no
  sense (but still follow the syntax).   Note that an optional item like
  [ <modifier> ] has a chance of being used, depending on the value of some
  randomly generated number.

  The program generates and outputs one random sentence every three seconds until
  it is halted (for example, by typing Control-C in the terminal window where it is
  running).
 */


import connectors.SentenceSourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static connectors.SentenceSourceConnector.*;

public class SentenceGenerator {

    private static final Logger log = LoggerFactory.getLogger(SentenceGenerator.class);


    static final private String[] default_nouns = { "farmer", "rooster", "judge", "man", "maiden",
            "cow", "dog", "cat", "cheese", "kafka", "stream", "source", "sink" };

    static final private String[] default_verbs = { "kept", "waked", "married",
            "milked", "tossed", "chased", "lay in", "enrich", "ingest" };

    static final private String[] default_modifiers = { "that crowed in the morn", "sowing his corn",
            "all shaven and shorn",
            "all forlorn", "with the crumpled horn" };


    final String[] nouns;
    final String[] verbs;
    final String[] modifiers;

    final StringBuffer stringBuffer = new StringBuffer(64);

    public SentenceGenerator() {
        this(null, null, null);
    }

    public SentenceGenerator(String[] nouns, String[] verbs, String[] modifiers) {
        this.nouns = (nouns == null) ? default_nouns : nouns;
        this.verbs = (verbs == null) ? default_verbs : verbs;
        this.modifiers = (modifiers == null) ? default_modifiers : modifiers;
    }

    /**
     * The main routine prints out one random sentence every three
     * seconds, forever (or until the program is killed).
     */
    public static void main(String[] args) {
        SentenceGenerator sentenceGenerator = new SentenceGenerator();
        while (true) {
            System.out.println(sentenceGenerator.nextSentence());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
    }

    public String nextSentence() {
        clearCache();
        return randomSentence();
    }

    void clearCache() {
        stringBuffer.setLength(0);
    }

    /**
     * Generate a random sentence, following the grammar rule for a sentence.
     */
    String randomSentence() {

        /* A simple sentence */

        randomSimpleSentence();

        /* Optionally, "and" followed by another simple sentence.*/

        if (Math.random() > 0.75) { // 25% of sentences continue with another clause.
            stringBuffer.append(" and ");
            randomSimpleSentence();
        }

        return stringBuffer.toString();
    }

    /**
     * Generate a random simple_sentence, following the grammar rule for a simple_sentence.
     */
    void randomSimpleSentence() {

        /* "this is", optionally followed by a noun phrase, followed by "the house that Jack built. */

        stringBuffer.append("this is ");
        if (Math.random() > 0.15) { // 85% of sentences have a noun phrase.
            randomNounPhrase();
        }
        stringBuffer.append("the house that Jack built");
    }

    /**
     * Generates a random noun_phrase, following the grammar rule for a noun_phrase.
     */
    void randomNounPhrase() {

        /* A random noun. */

        int n = (int)(Math.random() * nouns.length);
        stringBuffer.append("the ").append(nouns[n]);

        /* Optionally, a modifier. */

        if (Math.random() > 0.75) { // 25% chance of having a modifier.
            int m = (int)(Math.random()* modifiers.length);
            stringBuffer.append(" ").append(modifiers[m]);
        }

        /* "that", followed by a random verb */

        int v = (int)(Math.random()* verbs.length);
        stringBuffer.append(" that ").append(verbs[v]).append(" ");

        /* Another random noun phrase */

        if (Math.random() > 0.5) {  // 50% chance of having another noun phrase.
            randomNounPhrase();
        }
    }
}
