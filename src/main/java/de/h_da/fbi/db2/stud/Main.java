package de.h_da.fbi.db2.stud;

import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.h_da.fbi.db2.tools.CsvDataReader;
import de.h_da.fbi.db2.entity.Category;
import de.h_da.fbi.db2.entity.Question;
import de.h_da.fbi.db2.entity.Answer;

/**
 * Main Class.
 * @version 0.1.1
 * @since 0.1.0
 * @author A. Hofmann
 * @author B.-A. Mokroß
 */

public class Main {
    /**
     * Main Method and Entry-Point
     * @param args Command-Line Arguments.
     */
    public static void main(String[] args) {
        try {
            // Read default csv
            final List<String[]> defaultCsvLines = CsvDataReader.read();
            
            // Read (if available) additional csv-files and default csv-file
            List<String> availableFiles = CsvDataReader.getAvailableFiles();
            for (String availableFile : availableFiles) {
                final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
            }
            
            /**
             * Reading from default CSV into a TreeSet
             * to avoid duplicates and to sort Category
             */
            final Set<String> categorySet = new TreeSet<>();
            for (String[] csv : defaultCsvLines) {
                String name = Arrays.asList(csv[7]).toString();
                if (name.contains("_kategorie")) {
                    continue;
                }
                name = name.substring(1, name.length() - 1);
                categorySet.add(name);
            }
            
            /**
             * Converts Set<String> categorySet 
             * to String[] categoryName
             */
            String[] categoryName = categorySet.toArray(new String[categorySet.size()]);
            
            /**
             * Converts String[] categoryName
             * to ArrayList<Category> categories
             */
            List<Category> categories = new ArrayList<>();
            for (String name : categoryName) {
                categories.add(new Category(name));
            }
            
            /**
             * Reads Question where its Category
             * match Category in current index
             */
            for (int i = 0; i < categories.size(); i++) {
                int qIndex = 0;   
                Category currentCat = categories.get(i);
                
                for (String[] csv : defaultCsvLines) {
                    // Reads id
                    String idString = Arrays.asList(csv[0]).toString();
                    
                    // Reads current category
                    String category = Arrays.asList(csv[7]).toString();                    
                    category = category.substring(1, category.length() - 1);
                    /**
                     * Skips when CSV contains "ID"
                     * or category doesn't match
                     */
                    if (idString.contains("ID")
                            || !(category.equals(currentCat.getName()))) {
                        continue;
                    }
                    
                    // Converts id to int
                    idString = idString.substring(1, idString.length() - 1);
                    int id = Integer.valueOf(idString);
                    
                    // Reads question text
                    String text = Arrays.asList(csv[1]).toString();
                    text = text.substring(1, text.length() - 1);
                    
                    // Inserts to ArrayList<Question> questions
                    currentCat.questions.add(new Question(id, text));
                    Question currentQue = currentCat.questions.get(qIndex);
                    
                    /**
                     * Reads Answer 1 to 4 for each Question
                     */
                    String[] answer = new String[4];
                    for (int j = 0; j < 4; j++) {
                        answer[j] = Arrays.asList(csv[2 + j]).toString();
                        answer[j] = answer[j].substring(1, answer[j].length() - 1);
                        currentQue.answers.add(new Answer(answer[j], false));
                    }
                    
                    String answerTrueString = Arrays.asList(csv[6]).toString();
                    answerTrueString = answerTrueString.substring(1, answerTrueString.length() - 1);
                    int answerTrue = Integer.valueOf(answerTrueString) - 1;
                    currentQue.answers.get(answerTrue).setCorrect(true);
                    
                    qIndex++;
                }
            }
            
            /**
             * Test print console
             */
            for (int i = 0; i < categories.size(); i++) {
                Category cat = categories.get(i);
                System.out.println((i + 1) + " " + cat.getName());
                for (int j = 0; j < cat.questions.size(); j++) {
                    Question que = cat.questions.get(j);
                    System.out.println("  [ID: " + 
                            que.getId() + "] " +
                            que.getText());
                    for (int k = 0; k < que.answers.size(); k++) {
                        Answer ans = que.answers.get(k);
                        String answerText = ans.getText();
                        /*System.out.println("    [" +
                                (k + 1) + "] " + 
                                ans.getText());*/
                        if (ans.getCorrect() == true) {
                            System.out.println("    [" + (k + 1) + "] " + answerText + "*");
                        } else {
                            System.out.println("    [" + (k + 1) + "] " + answerText);
                        }                           
                    }
                }
                System.out.println();
            }
            
            int amountQuestion = 0;
            for (int i = 0; i < categories.size(); i++) {
                amountQuestion += categories.get(i).questions.size();
            }
            System.out.println("Anzahl Kategorien: " + categories.size());
            System.out.println("Anzahl Fragen: " + amountQuestion);
            
            /**
             * Insert to database SQL
             */
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultPU");
            EntityManager em = emf.createEntityManager();
            /*
            em.getTransaction().begin();
            for (int i = 0; i < categories.size(); i++) {
                Category cat = categories.get(i);
                em.persist(cat);
                for (int j = 0; j < cat.questions.size(); j++) {
                    Question que = cat.questions.get(j);
                    que.setCategory(cat);
                    em.persist(que);
                    for (int k = 0; k < que.answers.size(); k++) {
                        Answer ans = que.answers.get(k);
                        ans.setQuestion(que);
                        em.persist(ans);
                    }
                }
            }
            em.getTransaction().commit();
            */
            
            /**
             * Test print from database SQL
             */
            /*
            // Print all categories
            List resultL = em.createQuery("select c from Category c").getResultList();
            for (Iterator i = resultL.iterator(); i.hasNext();) {
                Category cm = (Category) i.next();
                System.out.println(cm.getId() + "-" + cm.getName());
            }
            
            // Print category ID = 1
            Category result = (Category) em.createQuery("select c from Category c where c.catId = 1").getSingleResult();
            System.out.println(result.getName());
            
            // Print category ID = 5, questions ID
            List resultList = em.createQuery("select q, c from Question q join q.cat c "
                    + "where c.catId = 5").getResultList();
            if (resultList.isEmpty()) {
                System.out.println("Keine Datensätze ausgewählt");
            } else {
                for (Iterator i = resultList.iterator(); i.hasNext();) {
                    Object[] element = (Object[]) i.next();
                    System.out.println("Category: " + ((Category) element[1]).getId());
                    System.out.println("Questions: " + ((Question) element[0]).getId());
                }
            }
            */
            //em.close();
                       
        } catch (URISyntaxException use) {
            System.out.println(use);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new GUI().setVisible(true);
        });
                
    }
}