package com.mfodepositorsacc.dmodel;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by berz on 04.06.15.
 */
@Entity
@Table
@DiscriminatorValue("EMAIL")
public class EmailNotification extends Notification {
}
