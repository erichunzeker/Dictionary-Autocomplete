import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class DLB {
    private DLBNode root;
    private DLBNode suggestionRoot;


    public DLB() {
        root = new DLBNode(); //main dlb trie for dictionary
        suggestionRoot = new DLBNode(); //dlb trie for user history
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public void setTrie(String filename) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

            String word;

            while ((word = bufferedReader.readLine()) != null) {
                word += "$";
                if (filename.equals("dictionary.txt")) //ability to use function with history and dictionary
                    insert(word, root);
                else insert(word, suggestionRoot);
            }
        } catch (FileNotFoundException f) {
            System.out.println("File not Found");
        }

    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public void insert(String word, DLBNode root) {
        DLBNode curr = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (i > 0)
                while (curr.rightSib != null && curr.value != word.charAt(i - 1)) //find right sib to match current character
                    curr = curr.rightSib;

            if (curr.child == null) //if there is no child, it's a new word, so start building it
                addChild(curr, c);

            else if (curr.child.value != c && !searchSibling(curr.child, c)) //non existing word with no siblings, create new
                addSibling(curr.child, c);

            curr = curr.child; //go to next character in word
        }
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public void addSibling(DLBNode curr, char c) {
        while (curr.rightSib != null) //get to the end of the linked list to add
            curr = curr.rightSib;

        curr.rightSib = new DLBNode(c);
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public void addChild(DLBNode parent, char c) {
        parent.child = new DLBNode(c);
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public boolean searchSibling(DLBNode currLevel, char target) {

        while (currLevel != null) {
            if (currLevel.value == target) //traverse through level to find match
                return true;
            currLevel = currLevel.rightSib;
        }
        return false;
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public DLBNode search(String word, DLBNode root) {
        DLBNode curr = root;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (curr.child == null) //no word found
                return null;
            else if (curr.child.value == c) //next char has been found, move farther down trie
                curr = curr.child;

            else if (!searchSibling(curr.child, c)) //no match is found in the childs siblings aka word is not found
                return null;

            else if (curr.child.value != c && searchSibling(curr.child, c)) //there is a match but its a childs sibling so find that
            {
                curr = curr.child;
                while (curr.rightSib != null && curr.value != word.charAt(i))
                    curr = curr.rightSib;
            }
        }
        return curr;
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public ArrayList<String> getSuggestions(ArrayList<String> suggestions, String word, boolean type) {
        if (suggestions == null)
            suggestions = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(word);
        DLBNode curr;
        if (type)
            curr = search(word, root); //able to use function for history and dictionary tries
        else
            curr = search(word, suggestionRoot);

        if (findSuggestions(suggestions, curr, sb, 1)) // call recursive function
            return suggestions;
        else
            return suggestions;
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public boolean findSuggestions(ArrayList<String> arrayList, DLBNode curr, StringBuilder sb, int direction) {

        boolean done = false;
        while (arrayList.size() <= 5) {
            if (direction != 5) {
                if (arrayList.size() == 5) //only return 5 suggestions
                    return true;
                if (curr.value == '$') //word has been found
                {
                    sb.deleteCharAt(sb.length() - 1); //delete delimiting character ($)
                    if (!arrayList.contains(sb.toString())) //do not add duplicates
                        arrayList.add(sb.toString());
                } else {
                    if (direction != 2)
                        done = findSuggestions(arrayList, curr = curr.child, sb.append(curr.value), 3);

                    if (curr.rightSib != null)
                        direction = 2;
                    else
                        direction = 1;

                    if (!done && direction != 1)
                        done = findSuggestions(arrayList, curr = curr.rightSib, sb.append(curr.value), 1);

                    while (!done && curr.rightSib != null)
                        done = findSuggestions(arrayList, curr = curr.rightSib, sb.append(curr.value), 1);
                }

            }

            if (sb.length() > 0 && direction == 1 || sb.length() > 0 && direction == 2)
                sb.deleteCharAt(sb.length() - 1);

            return done;
        }
        return false;
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public void addToHistory(String word) throws IOException {
        try {
            FileWriter fileWriter = new FileWriter("user_history.txt", true); //write new word to history
            BufferedWriter fbw = new BufferedWriter(fileWriter);
            fbw.write(word);
            insert(word + "$", suggestionRoot); //add $ delimiter for trie purposes
            fbw.newLine();
            fbw.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    public void setMap(Map<String, Integer> history) throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("user_history.txt"));

            String word;

            while ((word = bufferedReader.readLine()) != null) //this is called at the start of every run
            {
                if (history.containsKey(word))
                    history.replace(word, history.get(word), (history.get(word) + 1)); //build key map to track frequency
                else
                    history.put(word, 1);

            }
        } catch (FileNotFoundException f) {
            System.out.println("File not Found");
        }
    }
    //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
}
