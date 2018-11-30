/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.h_da.fbi.db2.stud;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Timestamp;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.h_da.fbi.db2.entity.GameInformation;
import de.h_da.fbi.db2.entity.Category;
import de.h_da.fbi.db2.entity.Question;
import de.h_da.fbi.db2.entity.Answer;
import de.h_da.fbi.db2.entity.Player;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Rizki Rahman
 */
public class GUI extends javax.swing.JFrame {
    
    DefaultListModel DefaultListModelFromDatabase = new DefaultListModel();
    DefaultListModel selectedCategory = new DefaultListModel();
    
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultPU");
    EntityManager em = emf.createEntityManager();
    
    
    
    ArrayList<String> randomQuestions = new ArrayList<>();
    ArrayList<Integer> randomQuestionsId = new ArrayList<>();
    ArrayList<String> randomQuestionsCat = new ArrayList<>();
    
    int indexQuestion = 0;
    int score = 0;
    
    List<Player> players = new ArrayList<>();
    List<GameInformation> informations = new ArrayList<>();
    
    Timestamp start;
    Timestamp end;
    
    ArrayList<String> chosenAnswers = new ArrayList<>();
    
    List<Category> selectedCategoriesName = new ArrayList<>();
    List<Category> selectedCategoriesEntity = new ArrayList<>();
    List<Question> questionsForDB = new ArrayList<>();
    List<Answer> answersForDB = new ArrayList<>();
    
    Player existingPlayer;
    GameInformation infoForExistingPlayer;
    boolean existingPlayerBool;
    
    /**
     * Creates new form GameUI
     */
    public GUI() {
        initComponents();
        populateCategoriesList();
    }
    
    private List<Category> selectedCategoriesAsArrayList() {
        for (int i = 0; i < selected_List.getModel().getSize(); i++) {
            String categoryName;
            categoryName = selected_List.getModel().getElementAt(i);
            selectedCategoriesName.add(new Category(categoryName));
        }
        return selectedCategoriesName;
    }
    
    private void populateCategoriesList() {
        List resultL = em.createQuery("select c from Category c order by c.catId").getResultList();
        for (Iterator i = resultL.iterator(); i.hasNext();) {
            Category cm = (Category) i.next();
            DefaultListModelFromDatabase.addElement(cm.getName());            
        }
        available_List.setModel(DefaultListModelFromDatabase);
    }
    
    private void randomizeQuestion() {
        List<Category> catArray = selectedCategoriesAsArrayList();
        for (int i = 0; i < catArray.size(); i++) {
            
            List resultL = em.createQuery("select q, c from Question q join q.cat c where c.catName = :catName" )
                    .setParameter("catName", catArray.get(i).getName())
                    //.setMaxResults(Integer.valueOf(maxQuestion_TextField.getText()))
                    .getResultList();
            
            int qSize = Integer.valueOf(maxQuestion_TextField.getText());
            Collections.shuffle(resultL);
            
            if(qSize>resultL.size()){
                qSize = resultL.size();
            }
            for(int j=0; j<qSize; j++){
                Object[] element = (Object[]) resultL.get(j);
                randomQuestions.add(((Question) element[0]).getText());
                randomQuestionsId.add(((Question) element[0]).getId());
                randomQuestionsCat.add(((Category) element[1]).getName());
            }
            
            /*
            for (Iterator j = resultL.iterator(); j.hasNext();) {
                Object[] element = (Object[]) j.next();
                randomQuestions.add(((Question) element[0]).getText());
                randomQuestionsId.add(((Question) element[0]).getId());
                randomQuestionsCat.add(((Category) element[1]).getName());
            }
            */
        }
    }
    
    private void selectedCategoriesAsEntity() {
        for (int i = 0; i < selectedCategoriesName.size(); i++) {
            List resultL = em.createQuery("select c from Category c where c.catName = :catName")
                    .setParameter("catName", selectedCategoriesName.get(i).getName()).getResultList();
            for (Iterator j = resultL.iterator(); j.hasNext();) {
                Category cm = (Category) j.next();
                selectedCategoriesEntity.add(cm);
            }
        }
    }
    
    private void randomizedQuestionAsEntity() {
        for (int i = 0; i < randomQuestionsId.size(); i++) {
            List resultL = em.createQuery("select q from Question q where q.queId = :qID")
                    .setParameter("qID", randomQuestionsId.get(i)).getResultList();
            for (Iterator j = resultL.iterator(); j.hasNext();) {
                Question cm = (Question) j.next();
                questionsForDB.add(cm);
            }
        }        
    }
    
    private void chosenAnswerAsEntity() {
        for (int i = 0; i < randomQuestionsId.size(); i++) {
            List resultL = em.createQuery("select a, q from Answer a join a.que q where q.queId = :quesID and a.ansText = :aText")
                    .setParameter("quesID", randomQuestionsId.get(i))
                    .setParameter("aText", chosenAnswers.get(i)).getResultList();
            for (Iterator j = resultL.iterator(); j.hasNext();) {
                Object[] element = (Object[]) j.next();
                answersForDB.add(((Answer) element[0]));
            }
        }
    }
    
    private void setAnswersFromRandomQuestions() {
        ArrayList<String> answersFromRandomQuestions = new ArrayList<>();
        List resultL = em.createQuery("select a, q from Answer a join a.que q where q.queId = :quesID")
                .setParameter("quesID", randomQuestionsId.get(indexQuestion)).getResultList();
        for (Iterator j = resultL.iterator(); j.hasNext();) {
            Object[] element = (Object[]) j.next();
            answersFromRandomQuestions.add(((Answer) element[0]).getText());
        }
        for (int k = 0; k < answersFromRandomQuestions.size(); k++) {
            if (k == 0) {
                answer1_RadioButton.setText(answersFromRandomQuestions.get(k));
                answer1_RadioButton.setActionCommand(answersFromRandomQuestions.get(k));
            }
            if (k == 1) {
                answer2_RadioButton.setText(answersFromRandomQuestions.get(k));
                answer2_RadioButton.setActionCommand(answersFromRandomQuestions.get(k));
            }
            if (k == 2) {
                answer3_RadioButton.setText(answersFromRandomQuestions.get(k));
                answer3_RadioButton.setActionCommand(answersFromRandomQuestions.get(k));
            }
            if (k == 3) {
                answer4_RadioButton.setText(answersFromRandomQuestions.get(k));
                answer4_RadioButton.setActionCommand(answersFromRandomQuestions.get(k));
            }
        }
    }
    
    private void addGameInfoToDB() {
        
        selectedCategoriesAsEntity();
        randomizedQuestionAsEntity();
        chosenAnswerAsEntity();
        
        if (existingPlayerBool) {
            informations.add(new GameInformation(start, end, score, randomQuestions.size()));
            
            em.getTransaction().begin();
            
            for (int i = 0; i < informations.size(); i++) {
                informations.get(i).setPlayer(existingPlayer);
                for (int k = 0; k < selectedCategoriesEntity.size(); k++) {
                    informations.get(i).getCategories().add(selectedCategoriesEntity.get(k));
                }
                for (int l = 0; l < questionsForDB.size(); l++) {
                    informations.get(i).getQuestions().add(questionsForDB.get(l));
                }
                for (int m = 0; m < answersForDB.size(); m++) {
                    informations.get(i).getAnswers().add(answersForDB.get(m));
                }
                em.persist(informations.get(i));
            }
            em.getTransaction().commit();
        } else {
            for (int i = 0; i < players.size(); i++) {
                players.get(i).gameInfo.add((new GameInformation(start, end, score, randomQuestions.size())));
            }
            
            em.getTransaction().begin();
            for (int i = 0; i < players.size(); i++) {
                em.persist(players.get(i));
                for (int j = 0; j < players.get(i).gameInfo.size(); j++) {
                    players.get(i).gameInfo.get(j).setPlayer(players.get(i));
                    for (int k = 0; k < selectedCategoriesEntity.size(); k++) {
                        players.get(i).gameInfo.get(j).getCategories().add(selectedCategoriesEntity.get(k));
                    }
                    for (int l = 0; l < questionsForDB.size(); l++) {
                        players.get(i).gameInfo.get(j).getQuestions().add(questionsForDB.get(l));
                    }
                    for (int m = 0; m < answersForDB.size(); m++) {
                        players.get(i).gameInfo.get(j).getAnswers().add(answersForDB.get(m));
                    }
                    em.persist(players.get(i).gameInfo.get(j));
                }
            }
            em.getTransaction().commit();
        }
    }
    
    private void showResult() {
        // show result card
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "result_Card");
        
        Timestamp timestampEnd = new Timestamp(System.currentTimeMillis());
        end = timestampEnd;
        
        // show result
        greetings_Label.setText("Hallo, " + playerName_TextField.getText());
        number_Label.setText(score + "/" + String.valueOf(randomQuestions.size()));
        startTime_TextPane.setText(String.valueOf(start));
        endTime_TextPane.setText(String.valueOf(end));
    }
    
    private void nextQuestion() {
        if ((indexQuestion + 1) > randomQuestions.size()) {
            showResult();
            addGameInfoToDB();            
        }
        else {
            kategorieFrage_Label.setText("Kategorie: " + randomQuestionsCat.get(indexQuestion));
            idFrage_Label.setText("ID: " + randomQuestionsId.get(indexQuestion).toString());
            question_TextPane.setText(randomQuestions.get(indexQuestion));
            setAnswersFromRandomQuestions();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        answer_ButtonGroup = new javax.swing.ButtonGroup();
        mainMenu_Panel = new javax.swing.JPanel();
        wissenstest_Label = new javax.swing.JLabel();
        playerName_Label = new javax.swing.JLabel();
        playerName_TextField = new javax.swing.JTextField();
        start_Button = new javax.swing.JButton();
        massenDaten_Button = new javax.swing.JButton();
        button1 = new java.awt.Button();
        button2 = new java.awt.Button();
        category_Panel = new javax.swing.JPanel();
        kategorie_Label = new javax.swing.JLabel();
        available_Label = new javax.swing.JLabel();
        selected_Label = new javax.swing.JLabel();
        available_ScrollPane = new javax.swing.JScrollPane();
        available_List = new javax.swing.JList<>();
        selected_ScrollPane = new javax.swing.JScrollPane();
        selected_List = new javax.swing.JList<>();
        maxQuestion_Label = new javax.swing.JLabel();
        maxQuestion_TextField = new javax.swing.JTextField();
        nextKategorie_Button = new javax.swing.JButton();
        question_Panel = new javax.swing.JPanel();
        frage_Label = new javax.swing.JLabel();
        kategorieFrage_Label = new javax.swing.JLabel();
        idFrage_Label = new javax.swing.JLabel();
        question_ScrollPane = new javax.swing.JScrollPane();
        question_TextPane = new javax.swing.JTextPane();
        answer1_RadioButton = new javax.swing.JRadioButton();
        answer2_RadioButton = new javax.swing.JRadioButton();
        answer3_RadioButton = new javax.swing.JRadioButton();
        answer4_RadioButton = new javax.swing.JRadioButton();
        nextFrage_Button = new javax.swing.JButton();
        correct_Panel = new javax.swing.JPanel();
        frageLoesung_Label = new javax.swing.JLabel();
        kategorieFrageKorrekt_Label = new javax.swing.JLabel();
        idFrageKorrekt_Label = new javax.swing.JLabel();
        richtig_ScrollPane = new javax.swing.JScrollPane();
        richtig_TextPane = new javax.swing.JTextPane();
        playerAnswer_Label = new javax.swing.JLabel();
        playerAnswer_ScrollPane = new javax.swing.JScrollPane();
        playerAnswer_TextPane = new javax.swing.JTextPane();
        correctAnswer_Label = new javax.swing.JLabel();
        correctAnswer_ScrollPane = new javax.swing.JScrollPane();
        correctAnswer_TextPane = new javax.swing.JTextPane();
        nextCorrect_Button = new javax.swing.JButton();
        rightWrong_Label = new javax.swing.JLabel();
        result_Panel = new javax.swing.JPanel();
        ergebnis_Label = new javax.swing.JLabel();
        greetings_Label = new javax.swing.JLabel();
        score_Label = new javax.swing.JLabel();
        number_Label = new javax.swing.JLabel();
        startTime_Label = new javax.swing.JLabel();
        startTime_ScrollPane = new javax.swing.JScrollPane();
        startTime_TextPane = new javax.swing.JTextPane();
        endTime_Label = new javax.swing.JLabel();
        endTime_ScrollPane = new javax.swing.JScrollPane();
        endTime_TextPane = new javax.swing.JTextPane();
        restart_Button = new javax.swing.JButton();
        massdaten_Panel = new javax.swing.JPanel();
        anzahlSpieler_TextField = new javax.swing.JTextField();
        anzahlSpiel_TextField = new javax.swing.JTextField();
        anzahlSpieler_Label = new javax.swing.JLabel();
        anzahlSpiel_Label = new javax.swing.JLabel();
        loadMassdata_Button = new javax.swing.JButton();
        Analyse1_Button = new javax.swing.JButton();
        Analyse2_jButton = new javax.swing.JButton();
        Analyse3_Button = new javax.swing.JButton();
        Analyse4_Button = new javax.swing.JButton();
        Analyse1_Panel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_A1 = new javax.swing.JTable();
        startTime_TextField = new javax.swing.JTextField();
        endTime_TextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        searchPlayerPeriod_Button = new javax.swing.JButton();
        back1_Button = new javax.swing.JButton();
        Analyse2_Panel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_A2 = new javax.swing.JTable();
        searchPlayerGames_TextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        searchPlayerGames_Button = new javax.swing.JButton();
        back2_Button = new javax.swing.JButton();
        Analyse3_Panel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_A3 = new javax.swing.JTable();
        back3_Button = new javax.swing.JButton();
        Analyse4_Panel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_A4 = new javax.swing.JTable();
        back4_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.CardLayout());

        mainMenu_Panel.setPreferredSize(new java.awt.Dimension(800, 600));

        wissenstest_Label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        wissenstest_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wissenstest_Label.setLabelFor(mainMenu_Panel);
        wissenstest_Label.setText("Wissenstest");
        wissenstest_Label.setToolTipText("");

        playerName_Label.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        playerName_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        playerName_Label.setText("Name:");

        playerName_TextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        playerName_TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        playerName_TextField.setToolTipText("Geben Sie Ihren Name ein");

        start_Button.setText("Start");
        start_Button.setToolTipText("");
        start_Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        start_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_ButtonActionPerformed(evt);
            }
        });

        massenDaten_Button.setText("Massendaten Test");
        massenDaten_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                massenDaten_ButtonActionPerformed(evt);
            }
        });

        button1.setLabel("button1");

        button2.setLabel("button2");

        javax.swing.GroupLayout mainMenu_PanelLayout = new javax.swing.GroupLayout(mainMenu_Panel);
        mainMenu_Panel.setLayout(mainMenu_PanelLayout);
        mainMenu_PanelLayout.setHorizontalGroup(
            mainMenu_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(playerName_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(wissenstest_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainMenu_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(playerName_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMenu_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(start_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(mainMenu_PanelLayout.createSequentialGroup()
                .addGap(338, 338, 338)
                .addComponent(massenDaten_Button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainMenu_PanelLayout.setVerticalGroup(
            mainMenu_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMenu_PanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(wissenstest_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(playerName_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerName_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(massenDaten_Button)
                .addGap(77, 77, 77)
                .addComponent(start_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(mainMenu_Panel, "menu_Card");
        mainMenu_Panel.getAccessibleContext().setAccessibleName("");

        kategorie_Label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        kategorie_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kategorie_Label.setLabelFor(mainMenu_Panel);
        kategorie_Label.setText("Kategorie");
        kategorie_Label.setToolTipText("");

        available_Label.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        available_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        available_Label.setText("Verfügbare Kategorien");

        selected_Label.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        selected_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selected_Label.setText("Ausgewählte Kategorien");

        available_List.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                available_ListMouseClicked(evt);
            }
        });
        available_ScrollPane.setViewportView(available_List);

        selected_ScrollPane.setViewportView(selected_List);

        maxQuestion_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maxQuestion_Label.setText("max Fragen pro Kategorie:");

        maxQuestion_TextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                maxQuestion_TextFieldKeyTyped(evt);
            }
        });

        nextKategorie_Button.setText("Weiter");
        nextKategorie_Button.setToolTipText("");
        nextKategorie_Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextKategorie_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextKategorie_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout category_PanelLayout = new javax.swing.GroupLayout(category_Panel);
        category_Panel.setLayout(category_PanelLayout);
        category_PanelLayout.setHorizontalGroup(
            category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kategorie_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(category_PanelLayout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(available_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(available_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxQuestion_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                    .addGroup(category_PanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(maxQuestion_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(selected_Label, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(selected_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(91, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, category_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nextKategorie_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        category_PanelLayout.setVerticalGroup(
            category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(category_PanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(kategorie_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(category_PanelLayout.createSequentialGroup()
                        .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(available_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selected_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(category_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(available_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(selected_ScrollPane)))
                    .addGroup(category_PanelLayout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(maxQuestion_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxQuestion_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(99, 99, 99)
                .addComponent(nextKategorie_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(category_Panel, "category_Card");

        frage_Label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        frage_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frage_Label.setLabelFor(mainMenu_Panel);
        frage_Label.setText("Frage");
        frage_Label.setToolTipText("");

        kategorieFrage_Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        kategorieFrage_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kategorieFrage_Label.setText("Kategorie:");

        idFrage_Label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        idFrage_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        idFrage_Label.setText("ID:");

        question_ScrollPane.setViewportView(question_TextPane);

        answer_ButtonGroup.add(answer1_RadioButton);
        answer1_RadioButton.setText("Antwort 1");

        answer_ButtonGroup.add(answer2_RadioButton);
        answer2_RadioButton.setText("Antwort 2");

        answer_ButtonGroup.add(answer3_RadioButton);
        answer3_RadioButton.setText("Antwort 3");

        answer_ButtonGroup.add(answer4_RadioButton);
        answer4_RadioButton.setText("Antwort 4");

        nextFrage_Button.setText("Weiter");
        nextFrage_Button.setToolTipText("");
        nextFrage_Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextFrage_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextFrage_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout question_PanelLayout = new javax.swing.GroupLayout(question_Panel);
        question_Panel.setLayout(question_PanelLayout);
        question_PanelLayout.setHorizontalGroup(
            question_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frage_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(kategorieFrage_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(idFrage_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(question_PanelLayout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addGroup(question_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(question_ScrollPane)
                    .addComponent(answer1_RadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(answer2_RadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(answer3_RadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(answer4_RadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(150, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, question_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nextFrage_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        question_PanelLayout.setVerticalGroup(
            question_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(question_PanelLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(frage_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 23, Short.MAX_VALUE)
                .addComponent(kategorieFrage_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idFrage_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(question_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(answer1_RadioButton)
                .addGap(18, 18, 18)
                .addComponent(answer2_RadioButton)
                .addGap(18, 18, 18)
                .addComponent(answer3_RadioButton)
                .addGap(18, 18, 18)
                .addComponent(answer4_RadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addComponent(nextFrage_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(question_Panel, "question_Card");

        frageLoesung_Label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        frageLoesung_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frageLoesung_Label.setLabelFor(mainMenu_Panel);
        frageLoesung_Label.setText("Frage");
        frageLoesung_Label.setToolTipText("");

        kategorieFrageKorrekt_Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        kategorieFrageKorrekt_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kategorieFrageKorrekt_Label.setText("Kategorie:");

        idFrageKorrekt_Label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        idFrageKorrekt_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        idFrageKorrekt_Label.setText("ID:");

        richtig_ScrollPane.setViewportView(richtig_TextPane);

        playerAnswer_Label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        playerAnswer_Label.setText("Ihre Antwort :");

        playerAnswer_ScrollPane.setViewportView(playerAnswer_TextPane);

        correctAnswer_Label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        correctAnswer_Label.setText("Richtige Antwort :");

        correctAnswer_ScrollPane.setViewportView(correctAnswer_TextPane);

        nextCorrect_Button.setText("Weiter");
        nextCorrect_Button.setToolTipText("");
        nextCorrect_Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextCorrect_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextCorrect_ButtonActionPerformed(evt);
            }
        });

        rightWrong_Label.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        rightWrong_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rightWrong_Label.setText("Richtig!");

        javax.swing.GroupLayout correct_PanelLayout = new javax.swing.GroupLayout(correct_Panel);
        correct_Panel.setLayout(correct_PanelLayout);
        correct_PanelLayout.setHorizontalGroup(
            correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frageLoesung_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(kategorieFrageKorrekt_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(idFrageKorrekt_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(rightWrong_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(correct_PanelLayout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addGroup(correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(richtig_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(correct_PanelLayout.createSequentialGroup()
                        .addGroup(correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerAnswer_Label)
                            .addComponent(correctAnswer_Label))
                        .addGap(29, 29, 29)
                        .addGroup(correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(playerAnswer_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(correctAnswer_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(150, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, correct_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nextCorrect_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        correct_PanelLayout.setVerticalGroup(
            correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(correct_PanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(frageLoesung_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 28, Short.MAX_VALUE)
                .addComponent(kategorieFrageKorrekt_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idFrageKorrekt_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(richtig_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerAnswer_Label)
                    .addComponent(playerAnswer_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(correct_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(correctAnswer_Label)
                    .addComponent(correctAnswer_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(rightWrong_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addComponent(nextCorrect_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(correct_Panel, "correct_Card");

        ergebnis_Label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        ergebnis_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ergebnis_Label.setLabelFor(mainMenu_Panel);
        ergebnis_Label.setText("Ergebnis");
        ergebnis_Label.setToolTipText("");

        greetings_Label.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        greetings_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        greetings_Label.setText("Hallo, Player");

        score_Label.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        score_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        score_Label.setText("Score:");

        number_Label.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        number_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        number_Label.setText("5/5");

        startTime_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        startTime_Label.setText("Start Time:");

        startTime_TextPane.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        startTime_ScrollPane.setViewportView(startTime_TextPane);

        endTime_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        endTime_Label.setText("End Time:");

        endTime_ScrollPane.setViewportView(endTime_TextPane);

        restart_Button.setText("Start");
        restart_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restart_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout result_PanelLayout = new javax.swing.GroupLayout(result_Panel);
        result_Panel.setLayout(result_PanelLayout);
        result_PanelLayout.setHorizontalGroup(
            result_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ergebnis_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(score_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(number_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(startTime_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(endTime_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(greetings_Label, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(result_PanelLayout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .addGroup(result_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(endTime_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startTime_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(250, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, result_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(restart_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        result_PanelLayout.setVerticalGroup(
            result_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(result_PanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(ergebnis_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(greetings_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(score_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(number_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(startTime_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startTime_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(endTime_Label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endTime_ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addComponent(restart_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(result_Panel, "result_Card");

        anzahlSpieler_Label.setText("Anzahl Spieler");

        anzahlSpiel_Label.setText("Anzahl Spiel pro Spieler");

        loadMassdata_Button.setText("Massendaten generieren");
        loadMassdata_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMassdata_ButtonActionPerformed(evt);
            }
        });

        Analyse1_Button.setText("1. Analyse");
        Analyse1_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Analyse1_ButtonActionPerformed(evt);
            }
        });

        Analyse2_jButton.setText("2. Analyse");
        Analyse2_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Analyse2_jButtonActionPerformed(evt);
            }
        });

        Analyse3_Button.setText("3. Analyse");
        Analyse3_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Analyse3_ButtonActionPerformed(evt);
            }
        });

        Analyse4_Button.setText("4. Analyse");
        Analyse4_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Analyse4_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout massdaten_PanelLayout = new javax.swing.GroupLayout(massdaten_Panel);
        massdaten_Panel.setLayout(massdaten_PanelLayout);
        massdaten_PanelLayout.setHorizontalGroup(
            massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(massdaten_PanelLayout.createSequentialGroup()
                .addGroup(massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(massdaten_PanelLayout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(anzahlSpiel_Label)
                            .addComponent(anzahlSpieler_Label))
                        .addGap(66, 66, 66)
                        .addGroup(massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(anzahlSpieler_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(massdaten_PanelLayout.createSequentialGroup()
                                .addComponent(anzahlSpiel_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(120, 120, 120)
                                .addComponent(loadMassdata_Button))))
                    .addGroup(massdaten_PanelLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addGroup(massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Analyse2_jButton)
                            .addComponent(Analyse1_Button)
                            .addComponent(Analyse3_Button)
                            .addComponent(Analyse4_Button))))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        massdaten_PanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {anzahlSpiel_Label, anzahlSpieler_Label});

        massdaten_PanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {anzahlSpiel_TextField, anzahlSpieler_TextField});

        massdaten_PanelLayout.setVerticalGroup(
            massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(massdaten_PanelLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anzahlSpieler_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(anzahlSpieler_Label))
                .addGap(27, 27, 27)
                .addGroup(massdaten_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anzahlSpiel_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(anzahlSpiel_Label)
                    .addComponent(loadMassdata_Button))
                .addGap(130, 130, 130)
                .addComponent(Analyse1_Button)
                .addGap(35, 35, 35)
                .addComponent(Analyse2_jButton)
                .addGap(32, 32, 32)
                .addComponent(Analyse3_Button)
                .addGap(35, 35, 35)
                .addComponent(Analyse4_Button)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        getContentPane().add(massdaten_Panel, "massdaten_Card");

        Table_A1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(Table_A1);

        jLabel1.setText("Start");

        jLabel2.setText("End");

        jLabel3.setText("format : yyyy-mm-dd hh:mm:ss");

        searchPlayerPeriod_Button.setText("Suchen");
        searchPlayerPeriod_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPlayerPeriod_ButtonActionPerformed(evt);
            }
        });

        back1_Button.setText("Zurück");
        back1_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back1_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Analyse1_PanelLayout = new javax.swing.GroupLayout(Analyse1_Panel);
        Analyse1_Panel.setLayout(Analyse1_PanelLayout);
        Analyse1_PanelLayout.setHorizontalGroup(
            Analyse1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Analyse1_PanelLayout.createSequentialGroup()
                .addGap(146, 146, 146)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(281, 281, 281))
            .addGroup(Analyse1_PanelLayout.createSequentialGroup()
                .addGroup(Analyse1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Analyse1_PanelLayout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Analyse1_PanelLayout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(startTime_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(39, 39, 39)
                        .addComponent(endTime_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Analyse1_PanelLayout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addComponent(searchPlayerPeriod_Button)))
                .addContainerGap(215, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse1_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(back1_Button)
                .addGap(48, 48, 48))
        );
        Analyse1_PanelLayout.setVerticalGroup(
            Analyse1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse1_PanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(Analyse1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Analyse1_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startTime_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endTime_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(32, 32, 32)
                .addComponent(searchPlayerPeriod_Button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(back1_Button)
                .addGap(41, 41, 41))
        );

        getContentPane().add(Analyse1_Panel, "analyse1_Card");

        Table_A2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(Table_A2);

        jLabel4.setText("Spielername");

        searchPlayerGames_Button.setText("Suchen");
        searchPlayerGames_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPlayerGames_ButtonActionPerformed(evt);
            }
        });

        back2_Button.setText("Zurück");
        back2_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back2_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Analyse2_PanelLayout = new javax.swing.GroupLayout(Analyse2_Panel);
        Analyse2_Panel.setLayout(Analyse2_PanelLayout);
        Analyse2_PanelLayout.setHorizontalGroup(
            Analyse2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Analyse2_PanelLayout.createSequentialGroup()
                .addGroup(Analyse2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(Analyse2_PanelLayout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Analyse2_PanelLayout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(jLabel4)
                        .addGap(41, 41, 41)
                        .addComponent(searchPlayerGames_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchPlayerGames_Button)))
                .addContainerGap(274, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse2_PanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(back2_Button)
                .addGap(85, 85, 85))
        );
        Analyse2_PanelLayout.setVerticalGroup(
            Analyse2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse2_PanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(Analyse2_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchPlayerGames_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(searchPlayerGames_Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(back2_Button)
                .addGap(46, 46, 46))
        );

        getContentPane().add(Analyse2_Panel, "analyse2_Card");

        Table_A3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(Table_A3);

        back3_Button.setText("Zurück");
        back3_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back3_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Analyse3_PanelLayout = new javax.swing.GroupLayout(Analyse3_Panel);
        Analyse3_Panel.setLayout(Analyse3_PanelLayout);
        Analyse3_PanelLayout.setHorizontalGroup(
            Analyse3_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Analyse3_PanelLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(274, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse3_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(back3_Button)
                .addGap(107, 107, 107))
        );
        Analyse3_PanelLayout.setVerticalGroup(
            Analyse3_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse3_PanelLayout.createSequentialGroup()
                .addContainerGap(145, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(back3_Button)
                .addGap(44, 44, 44))
        );

        getContentPane().add(Analyse3_Panel, "analyse3_Card");

        Table_A4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(Table_A4);

        back4_Button.setText("Zurück");
        back4_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back4_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Analyse4_PanelLayout = new javax.swing.GroupLayout(Analyse4_Panel);
        Analyse4_Panel.setLayout(Analyse4_PanelLayout);
        Analyse4_PanelLayout.setHorizontalGroup(
            Analyse4_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Analyse4_PanelLayout.createSequentialGroup()
                .addGap(132, 132, 132)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(274, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse4_PanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(back4_Button)
                .addGap(80, 80, 80))
        );
        Analyse4_PanelLayout.setVerticalGroup(
            Analyse4_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Analyse4_PanelLayout.createSequentialGroup()
                .addContainerGap(137, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(back4_Button)
                .addGap(45, 45, 45))
        );

        getContentPane().add(Analyse4_Panel, "analyse4_Card");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void start_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_ButtonActionPerformed
        List playerList = em.createQuery("select p from Player p where p.playName = :pname")
                .setParameter("pname", playerName_TextField.getText()).getResultList();
        if (playerList.isEmpty()) {
            
        } else {
            Player pName = (Player) playerList.get(0);
            existingPlayer = pName;
            JOptionPane.showMessageDialog(null, "Willkommen zurück, " 
                    + pName.getName(), "Meldung", JOptionPane.INFORMATION_MESSAGE);
            existingPlayerBool = true;
        }
        
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "category_Card");
        
        String playername = playerName_TextField.getText();
        players.add(new Player(playername));
        
        Timestamp timestampStart = new Timestamp(System.currentTimeMillis());
        start = timestampStart;
    }//GEN-LAST:event_start_ButtonActionPerformed

    private void available_ListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_available_ListMouseClicked
        String selected = available_List.getSelectedValue();
        selectedCategory.addElement(selected);
        DefaultListModelFromDatabase.removeElement(selected);
        selected_List.setModel(selectedCategory);
    }//GEN-LAST:event_available_ListMouseClicked

    private void nextKategorie_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextKategorie_ButtonActionPerformed
        if(maxQuestion_TextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie max Anzahl der Fragen aus", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            CardLayout cl = (CardLayout) (getContentPane().getLayout());
            cl.show(getContentPane(), "question_Card");
            
            randomizeQuestion();
            nextQuestion();
        }
    }//GEN-LAST:event_nextKategorie_ButtonActionPerformed

    private void nextFrage_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextFrage_ButtonActionPerformed
        if (answer_ButtonGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie eine Antwort aus", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            CardLayout cl = (CardLayout) (getContentPane().getLayout());
            cl.show(getContentPane(), "correct_Card");
            
            kategorieFrageKorrekt_Label.setText("Kategorie: " + randomQuestionsCat.get(indexQuestion));
            idFrageKorrekt_Label.setText("ID: " + randomQuestionsId.get(indexQuestion));
            richtig_TextPane.setText(randomQuestions.get(indexQuestion));
            
            playerAnswer_TextPane.setText(answer_ButtonGroup.getSelection().getActionCommand());
            correctAnswer_TextPane.setText(randomQuestions.get(indexQuestion));
            
            chosenAnswers.add(answer_ButtonGroup.getSelection().getActionCommand());
            
            List checkAnswer = em.createQuery("select a, q from Answer a join a.que q where q.queId = :queID and a.ansText = :text")
                    .setParameter("queID", randomQuestionsId.get(indexQuestion))
                    .setParameter("text", answer_ButtonGroup.getSelection().getActionCommand())
                    .getResultList();
            for (Iterator j = checkAnswer.iterator(); j.hasNext();) {
                Object[] element = (Object[]) j.next();
                
                if(((Answer) element[0]).getCorrect()) {
                    rightWrong_Label.setText("Richtig!");
                    score += 1;
                } else {
                    rightWrong_Label.setText("Falsch!");
                }
            }
            List correctAnswer = em.createQuery("select a, q from Answer a join a.que q where q.queId = :quesID and a.ansCorrect = 1")
                    .setParameter("quesID", randomQuestionsId.get(indexQuestion)).getResultList();
            for (Iterator j = correctAnswer.iterator(); j.hasNext();) {
                Object[] element = (Object[]) j.next();
                correctAnswer_TextPane.setText(((Answer) element[0]).getText());
            }
            indexQuestion = indexQuestion + 1;
        }
    }//GEN-LAST:event_nextFrage_ButtonActionPerformed

    private void nextCorrect_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextCorrect_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "question_Card");
        
        answer_ButtonGroup.clearSelection();
        nextQuestion();
    }//GEN-LAST:event_nextCorrect_ButtonActionPerformed

    private void maxQuestion_TextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxQuestion_TextFieldKeyTyped
        char c = evt.getKeyChar();
        if (!(Character.isDigit(c) 
                || (c == KeyEvent.VK_BACK_SPACE) 
                || c==KeyEvent.VK_DELETE )) {
            evt.consume();
        }
    }//GEN-LAST:event_maxQuestion_TextFieldKeyTyped

    private void restart_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restart_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "menu_Card");
    }//GEN-LAST:event_restart_ButtonActionPerformed

    private void massenDaten_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_massenDaten_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "massdaten_Card");
    
        
    }//GEN-LAST:event_massenDaten_ButtonActionPerformed

    private void loadMassdata_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMassdata_ButtonActionPerformed
         if(anzahlSpieler_TextField.getText().isEmpty() || anzahlSpiel_TextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Bitte füllen Sie Anzahl der Spieler und Spiel aus", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
             
        List resultC = em.createQuery("select c from Category c").getResultList();
          long startTime = System.currentTimeMillis();   
             
        // Add Players
        int playerAmount = Integer.valueOf(anzahlSpieler_TextField.getText());
        
        
        for (int i = 0; i < playerAmount; i++){
            String pNameTest = "Spieler" + +i;
            players.add(new Player(pNameTest));
        }
        
        
       
        //Add GameInfo
        int gamesPerPlayer = Integer.valueOf(anzahlSpiel_TextField.getText());
        
        em.getTransaction().begin();
        for (int i = 0; i < players.size(); i++) {
          
             Calendar calendar = Calendar.getInstance();
           
            for(int o = 0; o < gamesPerPlayer; o++) {
                selectedCategoriesEntity.clear();
                questionsForDB.clear();
                answersForDB.clear();
               
                int dummyScore = 0;
                Timestamp dummyStart;
                Timestamp dummyEnd;
             
                //Add chosen Categories
                Collections.shuffle(resultC);
                for(int j = 0; j < 2; j++) {
                    Category cm = (Category) resultC.get(j);
                    selectedCategoriesEntity.add(cm);
                }
              
                //Add random Questions
                for(int j=0; j<selectedCategoriesEntity.size(); j++){
                    List resultQ = em.createNamedQuery("Question.findBySelectedCategories" )
                            .setParameter("catName", selectedCategoriesEntity.get(j).getName())
                            .setHint("eclipselink.query-results-cache", true)
                            .setMaxResults(2)
                            .getResultList();
                    for (int k = 0; k < resultQ.size(); k++) {
                        Object[] element = (Object[]) resultQ.get(k);
                        Question quest1 = (Question) element[0];
                        questionsForDB.add(quest1);
                            List<Answer> ans1 = (List) quest1.getAnswers();
                           Collections.shuffle(ans1);
                           
                            Answer ans2 = (Answer) ans1.get(0);
                                if(ans2.getCorrect()){
                                dummyScore += 1;
                                }
                            answersForDB.add(ans2);
                    }
                    
                }
               
               
                dummyStart = new Timestamp(calendar.getTimeInMillis());
                calendar.add(Calendar.HOUR, 1);
                dummyEnd = new Timestamp(calendar.getTimeInMillis());
                
               
                players.get(i).gameInfo.add((new GameInformation(dummyStart, dummyEnd, dummyScore, questionsForDB.size())));
                 
                
                players.get(i).gameInfo.get(o).setPlayer(players.get(i));
                for (int k = 0; k < selectedCategoriesEntity.size(); k++) {
                    players.get(i).gameInfo.get(o).getCategories().add(selectedCategoriesEntity.get(k));
                }
                for (int l = 0; l < questionsForDB.size(); l++) {
                    players.get(i).gameInfo.get(o).getQuestions().add(questionsForDB.get(l));
                }
                for (int m = 0; m < answersForDB.size(); m++) {
                    players.get(i).gameInfo.get(o).getAnswers().add(answersForDB.get(m));
                }
                
                 //Increment day by 1
                 calendar.add(Calendar.DATE, 1);
                
            }
            em.persist(players.get(i));
                for(int z = 0; z< players.get(i).gameInfo.size(); z++){
                    em.persist(players.get(i).gameInfo.get(z));
                     }
               em.flush();
               em.clear();
            
         }
        em.getTransaction().commit();
          long endTime   = System.currentTimeMillis();
            long totalTime = (endTime - startTime)/1000;
            JOptionPane.showMessageDialog(null, "Ladezeit : " 
                    + totalTime + " sekunden", "Meldung", JOptionPane.INFORMATION_MESSAGE);
         
         }
       
    }//GEN-LAST:event_loadMassdata_ButtonActionPerformed

    private void Analyse1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Analyse1_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "analyse1_Card");
     
    }//GEN-LAST:event_Analyse1_ButtonActionPerformed

    private void Analyse2_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Analyse2_jButtonActionPerformed
       CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "analyse2_Card");
        
    }//GEN-LAST:event_Analyse2_jButtonActionPerformed

    private void Analyse3_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Analyse3_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "analyse3_Card");
        
        List searchPlayerGamesAmount = em.createQuery("select p.playName, count(p.gameInfo) "
                + "from Player p "
                + "group by p.playName "
                + "order by count(p.gameInfo) desc")
                    .getResultList();
        
        DefaultTableModel A3_Model = new DefaultTableModel(new Object[]{"Player Name", "Games Played"}, 0);
         for (Iterator i = searchPlayerGamesAmount.iterator(); i.hasNext();) {
                Object[] element = (Object[]) i.next();
                //Player pl = (Player) element[0];
                A3_Model.addRow(new Object[]{element[0], element[1]});
            }
         Table_A3.setModel(A3_Model);
    }//GEN-LAST:event_Analyse3_ButtonActionPerformed

    private void Analyse4_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Analyse4_ButtonActionPerformed
         CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "analyse4_Card");
        
        List searchPlayerGamesAmount = em.createQuery("select c.catName, count(c.gameInfo) "
                + "from Category c "
                + "group by c.catName "
                + "order by count(c.gameInfo) desc")
                    .getResultList();
        
        DefaultTableModel A4_Model = new DefaultTableModel(new Object[]{"Category", "Times Selected"}, 0);
         for (Iterator i = searchPlayerGamesAmount.iterator(); i.hasNext();) {
                Object[] element = (Object[]) i.next();
                //Player pl = (Player) element[0];
                A4_Model.addRow(new Object[]{element[0], element[1]});
            }
         Table_A4.setModel(A4_Model);
    }//GEN-LAST:event_Analyse4_ButtonActionPerformed

    private void searchPlayerGames_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPlayerGames_ButtonActionPerformed
        
        List searchPlayerGames = em.createQuery("select g, p "
                + "from GameInformation g join g.players p "
                + "where p.playName = :playName")
                    .setParameter("playName", String.valueOf(searchPlayerGames_TextField.getText()))
                    .getResultList();
        
        DefaultTableModel A2_Model = new DefaultTableModel(new Object[]{"ID", "Start Date", "End Date", "Score", "Question Amounts"}, 0);
         for (Iterator i = searchPlayerGames.iterator(); i.hasNext();) {
                Object[] element = (Object[]) i.next();
                GameInformation gi = (GameInformation) element[0];
                A2_Model.addRow(new Object[]{gi.getId(), gi.getTimeStart(), gi.getTimeEnd(), gi.getScore(), gi.getQuestionAmount()});
            }
         Table_A2.setModel(A2_Model);
    }//GEN-LAST:event_searchPlayerGames_ButtonActionPerformed

    private void searchPlayerPeriod_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPlayerPeriod_ButtonActionPerformed
           
        List searchPlayer = em.createQuery("select g.players "
                + "from GameInformation g "
                + "where g.timeStart between :startDate and :endDate")
                .setParameter("startDate", Timestamp.valueOf(startTime_TextField.getText()))
                .setParameter("endDate", Timestamp.valueOf(endTime_TextField.getText()))
                .getResultList();
        
        DefaultTableModel A1_Model = new DefaultTableModel(new Object[]{"ID", "Player"}, 0);
         for (Iterator i = searchPlayer.iterator(); i.hasNext();) {
                Player cm = (Player) i.next();
                A1_Model.addRow(new Object[]{cm.getId(), cm.getName()});
            }
         Table_A1.setModel(A1_Model);
        
    }//GEN-LAST:event_searchPlayerPeriod_ButtonActionPerformed

    private void back1_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back1_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "massdaten_Card");
    }//GEN-LAST:event_back1_ButtonActionPerformed

    private void back2_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back2_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "massdaten_Card");
    }//GEN-LAST:event_back2_ButtonActionPerformed

    private void back3_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back3_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "massdaten_Card");
    }//GEN-LAST:event_back3_ButtonActionPerformed

    private void back4_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back4_ButtonActionPerformed
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "massdaten_Card");
    }//GEN-LAST:event_back4_ButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Analyse1_Button;
    private javax.swing.JPanel Analyse1_Panel;
    private javax.swing.JPanel Analyse2_Panel;
    private javax.swing.JButton Analyse2_jButton;
    private javax.swing.JButton Analyse3_Button;
    private javax.swing.JPanel Analyse3_Panel;
    private javax.swing.JButton Analyse4_Button;
    private javax.swing.JPanel Analyse4_Panel;
    private javax.swing.JTable Table_A1;
    private javax.swing.JTable Table_A2;
    private javax.swing.JTable Table_A3;
    private javax.swing.JTable Table_A4;
    private javax.swing.JRadioButton answer1_RadioButton;
    private javax.swing.JRadioButton answer2_RadioButton;
    private javax.swing.JRadioButton answer3_RadioButton;
    private javax.swing.JRadioButton answer4_RadioButton;
    private javax.swing.ButtonGroup answer_ButtonGroup;
    private javax.swing.JLabel anzahlSpiel_Label;
    private javax.swing.JTextField anzahlSpiel_TextField;
    private javax.swing.JLabel anzahlSpieler_Label;
    private javax.swing.JTextField anzahlSpieler_TextField;
    private javax.swing.JLabel available_Label;
    private javax.swing.JList<String> available_List;
    private javax.swing.JScrollPane available_ScrollPane;
    private javax.swing.JButton back1_Button;
    private javax.swing.JButton back2_Button;
    private javax.swing.JButton back3_Button;
    private javax.swing.JButton back4_Button;
    private java.awt.Button button1;
    private java.awt.Button button2;
    private javax.swing.JPanel category_Panel;
    private javax.swing.JLabel correctAnswer_Label;
    private javax.swing.JScrollPane correctAnswer_ScrollPane;
    private javax.swing.JTextPane correctAnswer_TextPane;
    private javax.swing.JPanel correct_Panel;
    private javax.swing.JLabel endTime_Label;
    private javax.swing.JScrollPane endTime_ScrollPane;
    private javax.swing.JTextField endTime_TextField;
    private javax.swing.JTextPane endTime_TextPane;
    private javax.swing.JLabel ergebnis_Label;
    private javax.swing.JLabel frageLoesung_Label;
    private javax.swing.JLabel frage_Label;
    private javax.swing.JLabel greetings_Label;
    private javax.swing.JLabel idFrageKorrekt_Label;
    private javax.swing.JLabel idFrage_Label;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel kategorieFrageKorrekt_Label;
    private javax.swing.JLabel kategorieFrage_Label;
    private javax.swing.JLabel kategorie_Label;
    private javax.swing.JButton loadMassdata_Button;
    private javax.swing.JPanel mainMenu_Panel;
    private javax.swing.JPanel massdaten_Panel;
    private javax.swing.JButton massenDaten_Button;
    private javax.swing.JLabel maxQuestion_Label;
    private javax.swing.JTextField maxQuestion_TextField;
    private javax.swing.JButton nextCorrect_Button;
    private javax.swing.JButton nextFrage_Button;
    private javax.swing.JButton nextKategorie_Button;
    private javax.swing.JLabel number_Label;
    private javax.swing.JLabel playerAnswer_Label;
    private javax.swing.JScrollPane playerAnswer_ScrollPane;
    private javax.swing.JTextPane playerAnswer_TextPane;
    private javax.swing.JLabel playerName_Label;
    private javax.swing.JTextField playerName_TextField;
    private javax.swing.JPanel question_Panel;
    private javax.swing.JScrollPane question_ScrollPane;
    private javax.swing.JTextPane question_TextPane;
    private javax.swing.JButton restart_Button;
    private javax.swing.JPanel result_Panel;
    private javax.swing.JScrollPane richtig_ScrollPane;
    private javax.swing.JTextPane richtig_TextPane;
    private javax.swing.JLabel rightWrong_Label;
    private javax.swing.JLabel score_Label;
    private javax.swing.JButton searchPlayerGames_Button;
    private javax.swing.JTextField searchPlayerGames_TextField;
    private javax.swing.JButton searchPlayerPeriod_Button;
    private javax.swing.JLabel selected_Label;
    private javax.swing.JList<String> selected_List;
    private javax.swing.JScrollPane selected_ScrollPane;
    private javax.swing.JLabel startTime_Label;
    private javax.swing.JScrollPane startTime_ScrollPane;
    private javax.swing.JTextField startTime_TextField;
    private javax.swing.JTextPane startTime_TextPane;
    private javax.swing.JButton start_Button;
    private javax.swing.JLabel wissenstest_Label;
    // End of variables declaration//GEN-END:variables
}
