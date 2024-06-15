package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlockedIp is a Querydsl query type for BlockedIp
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlockedIp extends EntityPathBase<BlockedIp> {

    private static final long serialVersionUID = -1788402242L;

    public static final QBlockedIp blockedIp1 = new QBlockedIp("blockedIp1");

    public final StringPath blockedIp = createString("blockedIp");

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> sequence = createNumber("sequence", Long.class);

    public QBlockedIp(String variable) {
        super(BlockedIp.class, forVariable(variable));
    }

    public QBlockedIp(Path<? extends BlockedIp> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlockedIp(PathMetadata metadata) {
        super(BlockedIp.class, metadata);
    }

}

