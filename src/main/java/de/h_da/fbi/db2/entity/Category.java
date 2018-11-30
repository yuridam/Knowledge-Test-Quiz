package de.h_da.fbi.db2.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;

@Entity
@Table(name = "CATEGORY")
public class Category {

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.catId;
        hash = 23 * hash + Objects.hashCode(this.catName);
        hash = 23 * hash + Objects.hashCode(this.questions);
        hash = 23 * hash + Objects.hashCode(this.gameInfo);
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
        final Category other = (Category) obj;
        if (this.catId != other.catId) {
            return false;
        }
        if (!Objects.equals(this.catName, other.catName)) {
            return false;
        }
        if (!Objects.equals(this.questions, other.questions)) {
            return false;
        }
        if (!Objects.equals(this.gameInfo, other.gameInfo)) {
            return false;
        }
        return true;
    }
    
    public Category() {}
    
    public Category(String _catName) {
        this.catName = _catName;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAT_ID")
    private int catId;
    
    @Column(name = "CAT_NAME")
    private String catName;
    
    @OneToMany(targetEntity = Question.class, mappedBy = "cat")
    public ArrayList<Question> questions = new ArrayList<>();
    
    @ManyToMany(mappedBy = "categories")
    public List<GameInformation> gameInfo = new ArrayList<>();
    
    // Getter
    public int getId() {
        return catId;
    }
    
    public String getName() {
        return catName;
    }
    
    public List<GameInformation> getGameInfo() {
        return gameInfo;
    }
    
    // Setter
    public void setId(int _catId) {
        this.catId = _catId;
    }
    
    public void setName(String _catName) {
        this.catName = _catName;
    }
    
    public void setGameInfo(List<GameInformation> _gameInfo) {
        this.gameInfo = _gameInfo;
    }
}