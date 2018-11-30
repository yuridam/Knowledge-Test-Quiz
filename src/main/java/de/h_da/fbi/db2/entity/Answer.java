package de.h_da.fbi.db2.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@Entity
@NamedQueries({
@NamedQuery(name="Answer.findByRandomedQuestion",
query="select a, q from Answer a join a.que q where q.queId = :quesID")})
@Table(name = "ANSWER")
public class Answer {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.ansId;
        hash = 97 * hash + Objects.hashCode(this.ansText);
        hash = 97 * hash + (this.ansCorrect ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.que);
        hash = 97 * hash + Objects.hashCode(this.gameInfo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Answer other = (Answer) obj;
        if (this.ansId != other.ansId) {
            return false;
        }
        if (this.ansCorrect != other.ansCorrect) {
            return false;
        }
        if (!Objects.equals(this.ansText, other.ansText)) {
            return false;
        }
        if (!Objects.equals(this.que, other.que)) {
            return false;
        }
        if (!Objects.equals(this.gameInfo, other.gameInfo)) {
            return false;
        }
        return true;
    }
    
    public Answer() {}
    
    public Answer(String _ansText, boolean _ansCorrect) {
        this.ansText = _ansText;
        this.ansCorrect = _ansCorrect;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ANS_ID")
    private int ansId;
    
    @Column(name = "ANS_TEXT")
    private String ansText;
    
    @Column(name = "ANS_CORRECT")
    private boolean ansCorrect;
       
    @ManyToOne(fetch = FetchType.LAZY)
    private Question que;
    
    @ManyToMany(mappedBy = "answers")
    public List <GameInformation> gameInfo = new ArrayList<>();
    
    // Getter     
    public int getId() {
        return ansId;
    }
    
    public String getText() {
        return ansText;
    }
    
    public boolean getCorrect() {
        return ansCorrect;
    }
   
    public Question getQuestion() {
        return que;
    }
    
    // Setter
    public void setId(int _ansId) {
        this.ansId = _ansId;
    }
        
    public void setText(String _ansText) {
        this.ansText = _ansText;
    }
    
    public void setCorrect(boolean _ansCorrect) {
        this.ansCorrect = _ansCorrect;
    }
        
    public void setQuestion(Question _que) {
        this.que = _que;
    }
}