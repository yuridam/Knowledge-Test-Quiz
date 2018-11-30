package de.h_da.fbi.db2.entity;

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;

@Entity
@Table(name = "GAMEINFO")
public class GameInformation {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.gameId;
        hash = 29 * hash + Objects.hashCode(this.timeStart);
        hash = 29 * hash + Objects.hashCode(this.timeEnd);
        hash = 29 * hash + this.score;
        hash = 29 * hash + this.questionAmount;
        hash = 29 * hash + Objects.hashCode(this.players);
        hash = 29 * hash + Objects.hashCode(this.categories);
        hash = 29 * hash + Objects.hashCode(this.questions);
        hash = 29 * hash + Objects.hashCode(this.answers);
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
        final GameInformation other = (GameInformation) obj;
        if (this.gameId != other.gameId) {
            return false;
        }
        if (this.score != other.score) {
            return false;
        }
        if (this.questionAmount != other.questionAmount) {
            return false;
        }
        if (!Objects.equals(this.timeStart, other.timeStart)) {
            return false;
        }
        if (!Objects.equals(this.timeEnd, other.timeEnd)) {
            return false;
        }
        if (!Objects.equals(this.players, other.players)) {
            return false;
        }
        if (!Objects.equals(this.categories, other.categories)) {
            return false;
        }
        if (!Objects.equals(this.questions, other.questions)) {
            return false;
        }
        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        return true;
    }
    
    public GameInformation() {}
    
    public GameInformation(Timestamp _timeStart, Timestamp _timeEnd,
            int _score, int _questionAmount) {
        this.timeStart = _timeStart;
        this.timeEnd = _timeEnd;
        this.score = _score;
        this.questionAmount = _questionAmount;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "GAME_ID")
    private int gameId;
    
    @Column(name = "TIME_START")
    private Timestamp timeStart;
    
    @Column(name = "TIME_END")
    private Timestamp timeEnd;
    
    @Column(name = "SCORE")
    private int score;
    
    @Column(name = "QUESTION_AMOUNT")
    private int questionAmount;
    
    @ManyToOne
    private Player players;
    
    @ManyToMany
    public List<Category> categories = new ArrayList<>();
    
    @ManyToMany
    public List<Question> questions = new ArrayList<>();
    
    @ManyToMany
    public List<Answer> answers = new ArrayList<>();
    
    // Getter
    public int getId() {
        return gameId;
    }
    
    public Timestamp getTimeStart() {
        return timeStart;
    }
    
    public Timestamp getTimeEnd() {
        return timeEnd;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getQuestionAmount() {
        return questionAmount;
    }
    
    public Player getPlayer() {
        return players;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public List<Answer> getAnswers() {
        return answers;
    }
    
    // Setter
    public void setTimeStart(Timestamp _timeStart) {
        this.timeStart = _timeStart;
    }
    
    public void setTimeEnd(Timestamp _timeEnd) {
        this.timeEnd = _timeEnd;
    }
    
    public void setScore(int _score) {
        this.score = _score;
    }
    
    public void setQuestionAmount(int _questionAmount) {
        this.questionAmount = _questionAmount;
    }
    
    public void setPlayer(Player _player) {
        this.players = _player;
    }
    
    public void setCategories(List<Category> _categories) {
        this.categories = _categories;
    }
    
    public void setQuestions(List<Question> _questions) {
        this.questions = _questions;
    }
    
    public void setAnswers(List<Answer> _answers) {
        this.answers = _answers;
    }
}