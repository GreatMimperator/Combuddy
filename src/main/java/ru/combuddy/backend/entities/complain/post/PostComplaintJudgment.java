package ru.combuddy.backend.entities.complain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.complain.BaseComplaintJudgment;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"judge_id", "complaint_id"}))
public class PostComplaintJudgment extends BaseComplaintJudgment {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="complaint_id")
    private PostComplaint complaint;
}
