package com.codementor.ideapool.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "db_idea")
public class Idea {

    @Id
    @Column(name = "id")
    @GenericGenerator(name = "random_string_id", strategy = "com.codementor.ideapool.utils.RandomStringIdGenerator")
    @GeneratedValue(generator = "random_string_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content")
    private String content;

    @Column(name = "impact")
    @Min(1)
    @Max(10)
    private int impact;

    @Column(name = "ease")
    @Min(1)
    @Max(10)
    private int ease;

    @Column(name = "confidence")
    @Min(1)
    @Max(10)
    private int confidence;

    @Column(name = "average_score")
    private float averageScore;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public String getId() {
        return id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (!user.getIdeas().contains(this)) {
            user.getIdeas().add(this);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImpact() {
        return impact;
    }

    public void setImpact(int impact) {
        this.impact = impact;
    }

    public int getEase() {
        return ease;
    }

    public void setEase(int ease) {
        this.ease = ease;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    @JsonProperty("average_score")
    public float getAverageScore() {
        return averageScore;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.averageScore = (0f + impact + ease + confidence) / 3;
    }

    @PreUpdate
    protected void onUpdate() {
        this.averageScore = (0f + impact + ease + confidence) / 3;
    }

    @JsonProperty("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

}
