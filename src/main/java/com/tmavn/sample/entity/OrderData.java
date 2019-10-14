package com.tmavn.sample.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"stateChangeNotifies"})
@Entity
@Table(name = "order_data")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"stateChangeNotifies","notes"})
public class OrderData implements Serializable {

    public static final String STATE_SCHEDULED = "Scheduled";
    public static final String STATE_PROCESSING = "Processing";
    public static final String STATE_COMPLETED = "Completed";
    public static final String STATE_FAILED = "Failed";
    /**
     * 
     */
    private static final long serialVersionUID = 1126459286538409164L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    private String id;

    @Column
    private String description;

    @OneToMany(mappedBy = "orderData", cascade = CascadeType.ALL)
    private Set<Note> notes;

    @Column(nullable = false)
    private String state;

    @Column(name = "order_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String orderDate;

    @Column(name = "modify_date", nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String modifyDate;

    @OneToMany(mappedBy = "triggerData")
    private Set<StateChangeNotify> stateChangeNotifies;

}
