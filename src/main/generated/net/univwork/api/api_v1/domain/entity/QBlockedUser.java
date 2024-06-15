package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlockedUser is a Querydsl query type for BlockedUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlockedUser extends EntityPathBase<BlockedUser> {

    private static final long serialVersionUID = -667272542L;

    public static final QBlockedUser blockedUser1 = new QBlockedUser("blockedUser1");

    public final StringPath blockedUser = createString("blockedUser");

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> sequence = createNumber("sequence", Long.class);

    public QBlockedUser(String variable) {
        super(BlockedUser.class, forVariable(variable));
    }

    public QBlockedUser(Path<? extends BlockedUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlockedUser(PathMetadata metadata) {
        super(BlockedUser.class, metadata);
    }

}

