package com.example.bookmanagementsystembo.department.entity;

import com.example.bookmanagementsystembo.user.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name ="department")
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    @Comment("부서 ID")
    private Long departmentId;

    @Column(name = "name", nullable = false, length = 50)
    @Comment("부서 이름")
    private String name;
}
