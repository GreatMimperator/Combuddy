package ru.combuddy.backend.entities.complain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.complain.BaseComplaint;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class UserComplaint extends BaseComplaint {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="suspect_id", nullable = false)
    private UserAccount suspect;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "complaint")
    private List<UserComplaintJudgment> judgments;
}
