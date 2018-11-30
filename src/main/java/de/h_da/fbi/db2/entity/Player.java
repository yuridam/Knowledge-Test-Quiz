package de.h_da.fbi.db2.entity;

import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;

@Entity
@Table(name = "PLAYER")
public class Player {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.playId;
        hash = 19 * hash + Objects.hashCode(this.playName);
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
        final Player other = (Player) obj;
        if (this.playId != other.playId) {
            return false;
        }
        if (!Objects.equals(this.playName, other.playName)) {
            return false;
        }
        if (!Objects.equals(this.gameInfo, other.gameInfo)) {
            return false;
        }
        return true;
    }
    
    public Player() {}
    
    public Player(String _playName) {
        this.playName = _playName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PLAYER_ID")
    private int playId;
    
    @Column(name = "PLAYER_NAME")
    private String playName;
    
    @OneToMany(targetEntity = GameInformation.class, mappedBy = "players", fetch = FetchType.LAZY)
    public ArrayList<GameInformation> gameInfo = new ArrayList<>();
    
    // Getter
    public int getId() {
        return playId;
    }
    
    public String getName() {
        return playName;
    }
    
    // Setter
    public void setName(String _playName) {
        this.playName = _playName;
    }
}