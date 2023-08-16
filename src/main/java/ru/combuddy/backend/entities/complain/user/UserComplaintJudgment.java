package ru.combuddy.backend.entities.complain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.complain.BaseComplaintJudgment;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"judge_id", "complaint_id"}))
public class UserComplaintJudgment extends BaseComplaintJudgment {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="complaint_id")
    private UserComplaint complaint;
}
