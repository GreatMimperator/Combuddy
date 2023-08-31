package ru.combuddy.backend.repositories.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.entities.post.FavoritePost;
import ru.combuddy.backend.entities.post.Post;
import ru.combuddy.backend.entities.post.tag.PostTag;
import ru.combuddy.backend.entities.user.BlackList;
import ru.combuddy.backend.entities.user.Subscription;
import ru.combuddy.backend.entities.user.UserAccount;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Service
@AllArgsConstructor
public class PostCriteriaRepository {

    private final EntityManager entityManager;

    @Data
    @AllArgsConstructor
    @Builder
    public static class SearchParams {
        @Builder.Default
        private List<String> includedTagNames = new LinkedList<>();
        @Builder.Default
        private List<String> excludedTagNames = new LinkedList<>();
        @Builder.Default
        private List<Post.State> allowedStates = new LinkedList<>();
        private UserAccount receiver;
        @Builder.Default
        private boolean filterSubscriptions = false;
        @Builder.Default
        private boolean filterFavourites = false;
        @Builder.Default
        private boolean filterAggressors = false;
        private final Pageable pageable;
    }

    public Stream<Long> searchBy(SearchParams searchParams) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var postRoot = cq.from(Post.class);
        cq.select(postRoot.get("id"));
        var allPredicates = cb.and(
                getIncludesTagsPredicate(searchParams, cq, postRoot, cb),
                getExcludesTagsPredicate(searchParams, cq, postRoot, cb),
                stateIn(searchParams.allowedStates, postRoot),
                ownerIsNotAggressor(searchParams, cq, postRoot, cb),
                subscriptionsOnly(searchParams, cq, postRoot, cb),
                favouritesOnly(searchParams, cq, postRoot, cb)
        );
        cq.where(allPredicates);
        cq.orderBy(cb.desc(postRoot.get("creationDate")));
        var query = entityManager.createQuery(cq);
        applyPagination(query, searchParams.pageable);
        return query.getResultStream();
    }

    private Predicate getIncludesTagsPredicate(SearchParams searchParams,
                                               CriteriaQuery<?> cq,
                                               Root<Post> postRoot,
                                               CriteriaBuilder cb) {
        if (searchParams.includedTagNames.isEmpty()) {
            return cb.conjunction();
        }
        return getTagsIsInPredicatedSubquery(searchParams.includedTagNames, cq, postRoot, cb);
    }

    private Predicate getExcludesTagsPredicate(SearchParams searchParams,
                                               CriteriaQuery<?> cq,
                                               Root<Post> postRoot,
                                               CriteriaBuilder cb) {
        if (searchParams.excludedTagNames.isEmpty()) {
            return cb.conjunction();
        }
        return cb.not(getTagsIsInPredicatedSubquery(searchParams.excludedTagNames, cq, postRoot, cb));
    }

    private Predicate getTagsIsInPredicatedSubquery(List<String> tags,
                                                    CriteriaQuery<?> cq,
                                                    Root<Post> postRoot,
                                                    CriteriaBuilder cb) {
        var includedTagsSubquery = cq.subquery(PostTag.class);
        var includedTagsRoot = includedTagsSubquery.from(PostTag.class);
        var includedTagsJoin = includedTagsRoot.join("tag");
        var includedTagsIsInPredicate = includedTagsJoin.get("name").in(tags);
        var includedTagsIsThisPostPredicate = cb.equal(includedTagsRoot.get("post"), postRoot);
        includedTagsSubquery.where(cb.and(includedTagsIsInPredicate, includedTagsIsThisPostPredicate));
        return cb.exists(includedTagsSubquery);
    }

    private Predicate favouritesOnly(SearchParams searchParams,
                                     CriteriaQuery<?> cq,
                                     Root<Post> postRoot,
                                     CriteriaBuilder cb) {
        if (!searchParams.filterFavourites) {
            return cb.conjunction();
        }
        assertNotNull(searchParams.receiver);
        var favouritesSubquery = cq.subquery(Post.class);
        var favouritesRoot = favouritesSubquery.from(FavoritePost.class);
        favouritesSubquery.select(favouritesRoot.get("post"));
        favouritesSubquery.where(cb.equal(favouritesRoot.get("subscriber"), searchParams.receiver));
        return postRoot.in(favouritesSubquery);
    }
    private Predicate subscriptionsOnly(SearchParams searchParams,
                                        CriteriaQuery<?> cq,
                                        Root<Post> postRoot,
                                        CriteriaBuilder cb) {
        if (!searchParams.filterSubscriptions) {
            return cb.conjunction();
        }
        assertNotNull(searchParams.receiver);
        var subscriptionSubquery = cq.subquery(UserAccount.class);
        var subscriptionRoot = subscriptionSubquery.from(Subscription.class);
        subscriptionSubquery.select(subscriptionRoot.get("poster"));
        subscriptionSubquery.where(cb.equal(subscriptionRoot.get("subscriber"), searchParams.receiver));
        return postRoot.get("owner").in(subscriptionSubquery);
    }

    private Predicate ownerIsNotAggressor(SearchParams searchParams,
                                          CriteriaQuery<?> cq,
                                          Root<Post> postRoot,
                                          CriteriaBuilder cb) {
        if (!searchParams.filterAggressors) {
            return cb.conjunction();
        }
        assertNotNull(searchParams.receiver);
        var blacklistSubquery = cq.subquery(UserAccount.class);
        var blacklistRoot = blacklistSubquery.from(BlackList.class);
        blacklistSubquery.select(blacklistRoot.get("aggressor"));
        blacklistSubquery.where(cb.equal(blacklistRoot.get("defended"), searchParams.receiver));
        return cb.not(postRoot.get("owner").in(blacklistSubquery));
    }

    private Predicate stateIn(List<Post.State> statesList,
                              Root<Post> postRoot) {
        return postRoot.get("state").in(statesList);
    }


    private void applyPagination(TypedQuery<?> query, Pageable pageable) {
        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
    }
}
