package de.h_da.fbi.db2.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
@NamedQuery(name="Question.findBySelectedCategories",
query="select q, c from Question q join q.cat c where c.catName = :catName")})
@Table(name = "QUESTION")
public class Question {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.queId;
        hash = 19 * hash + Objects.hashCode(this.queText);
        hash = 19 * hash + Objects.hashCode(this.answers);
        hash = 19 * hash + Objects.hashCode(this.cat);
        hash = 19 * hash + Objects.hashCode(this.gameInfo);
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
        final Question other = (Question) obj;
        if (this.queId != other.queId) {
            return false;
        }
        if (!Objects.equals(this.queText, other.queText)) {
            return false;
        }
        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        if (!Objects.equals(this.cat, other.cat)) {
            return false;
        }
        if (!Objects.equals(this.gameInfo, other.gameInfo)) {
            return false;
        }
        return true;
    }
    
    public Question() {}
    
    public Question(int _queId, String _queText) {
        this.queId = _queId;
        this.queText = _queText;
    }
    
    @Id
    private int queId;
    
    @Column(name = "QUE_TEXT")
    private String queText;
    
    @OneToMany(targetEntity = Answer.class, mappedBy = "que")
    public ArrayList<Answer> answers = new ArrayList<>();
    public List<Answer> getAnswers() {
    return answers; 
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Category cat;
    
    @ManyToMany(mappedBy = "questions")
    public List<GameInformation> gameInfo = new ArrayList<>();

    // Getter
    public int getId() {
        return queId;
    }

    public String getText() {
        return queText;
    }
    
    public Category getCategory() {
        return cat;
    }
    
    // Setter
    public void setId(int _queId) {
        this.queId = _queId;
    }
    
    public void setText(String _queText) {
        this.queText = _queText;
    }
    
    public void setCategory(Category _cat) {
        this.cat = _cat;
    }
}