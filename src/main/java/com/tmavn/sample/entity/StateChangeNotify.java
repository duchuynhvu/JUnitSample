package com.tmavn.sample.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString(exclude = "triggerData")
@Table(name = "state_change_notify")
public class StateChangeNotify implements Serializable{

    public static final String TYPE_STATE_CHANGE_NOTIFY = "StateChangeNotify";
    
    /**
     * 
     */
    private static final long serialVersionUID = -3483172156441191010L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "trigger_id")
    private String triggerId;
    
    @Column(name = "trigger_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggerTime;
    
    @Column(name = "trigger_type")
    private String triggerType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderData triggerData;

}
