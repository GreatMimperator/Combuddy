package ru.combuddy.backend.entities.complain.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.combuddy.backend.entities.complain.BaseComplaint;
import ru.combuddy.backend.entities.post.Post;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class PostComplaint extends BaseComplaint {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="suspect_id", nullable = false)
    private Post suspectPost;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "complaint")
    private List<PostComplaintJudgment> judgments;
}
