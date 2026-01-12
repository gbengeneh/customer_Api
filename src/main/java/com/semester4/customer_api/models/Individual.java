package com.semester4.customer_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
@Entity
@Table(name = "Individual")
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Individual extends Customer implements Serializable{
    @Enumerated(EnumType.STRING)
    @Column(name = "Gender")
    private Gender gender;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "DOB")
    private LocalDate dateOfBirth;
}
