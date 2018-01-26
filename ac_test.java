import java.io.File;
import java.io.IOException;
import java.util.*;


public class ac_test
{

    public static void main(String[] args) throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> suggestions = null;
        ArrayList<String> suggestionsHistory;

        DLB dlb = new DLB();
        Map <String, Integer> history = new HashMap<>();


        final char EXIT = '!';
        char input;
        int count = 0;
        long startTime, estimatedTime, sum = 0;
        String word = "";

        File file = new File("user_history.txt");

        if(!file.exists())
            file.createNewFile();

        dlb.setTrie("dictionary.txt"); //build all of the tries and map
        dlb.setMap(history);
        dlb.setTrie("user_history.txt");


        System.out.print("Enter your first character:  ");

        do{
            input = scanner.next().charAt(0);

            if(input == '$' && count > 0)
            {
                dlb.addToHistory(word);
                history.put(word, 1); //initialize new word into history with occurances as 1
                System.out.print("\n\n ADDED " + word + " TO USER HISTORY"
                        + "\n\nEnter first character of the next word:  ");
                word = "";

            }

            else if((int)(input) < 54 && (int)(input) > 48 && count > 0)
            {
                dlb.addToHistory(suggestions.get((int)(input) - 49)); //input between 48 and 54 is ascii for 1(incl)-6(excl)

                if(history.containsKey(suggestions.get((int)(input) - 49)))
                    history.replace(suggestions.get((int)(input) - 49), history.get(suggestions.get((int)(input) - 49)),
                            (history.get(suggestions.get((int)(input) - 49))+1)); //replace old freq with freq++
                else
                    history.put(suggestions.get((int)(input) - 49), 1);
                System.out.print("\n\n WORD COMPLETED:  " + suggestions.get((int)(input) - 49)
                        + "\n\nEnter first character of the next word:  ");
                word = "";
            }



            else if(input != EXIT)
            {
                if(suggestions != null)
                    suggestions.clear();
                word += input;
                startTime = System.nanoTime();

                try{
                    suggestionsHistory = dlb.getSuggestions(suggestions, word, false); //get suggestions with current prefix

                    for(int i = 1; i < suggestionsHistory.size(); i++) //bubble sort type thing? lol. sort the suggestions by frequency
                    {
                        Integer first = history.get(suggestionsHistory.get(i));
                        Integer second = history.get(suggestionsHistory.get(i - 1));

                        if(first > second)
                        {
                            String temp = suggestionsHistory.get(i);
                            suggestionsHistory.remove(i);
                            suggestionsHistory.add(i - 1, temp);
                        }
                    }

                    suggestions = suggestionsHistory; //set history as first suggestions (sorted by frequency)
                }
                catch (NullPointerException e) {
                }

                try{
                    suggestions = dlb.getSuggestions(suggestions, word, true); //get dictionary suggestions
                }
                catch (NullPointerException e) {
                }

                estimatedTime = System.nanoTime() - startTime;
                sum += estimatedTime;
                Iterator<String> suggestionIterator = suggestions.iterator();

                if(!suggestions.isEmpty())
                {
                    System.out.println("\n(" + estimatedTime / 1000000000.0 + "s)" + "\nPredictions:");

                    for (int i = 0; i < suggestions.size(); i++)
                        System.out.print("(" + (i + 1) + ") " + suggestionIterator.next() + "    ");

                }
                else
                    System.out.println("\n\nNo Predictions");

                count++;
                System.out.print("\n\nEnter the next character:  ");
            }

        }while(input != EXIT);

        System.out.println("\n\nAverage Time:  " + (sum/count)/1000000000.0 + "s \nBye!");
    }
}