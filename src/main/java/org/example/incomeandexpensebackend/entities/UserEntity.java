package org.example.incomeandexpensebackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.incomeandexpensebackend.enums.RoleEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "isActive")
    private Boolean isActive;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<TransactionEntity> transactions = new ArrayList<>();


public void addTransaction(TransactionEntity transaction) {
    transactions.add(transaction);
    transaction.setUser(this);
}

public void removeTransaction(TransactionEntity transaction) {
    transactions.remove(transaction);
    transaction.setUser(null);
}
}
