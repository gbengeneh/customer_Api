package com.semester4.customer_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Entity
@Table(name = "Corporate")
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Corporate extends Customer implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = "Company_Type")
    private CompanyType companyType;
}
