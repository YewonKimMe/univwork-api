package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWorkplaceComment is a Querydsl query type for WorkplaceComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkplaceComment extends EntityPathBase<WorkplaceComment> {

    private static final long serialVersionUID = -1248774914L;

    public static final QWorkplaceComment workplaceComment = new QWorkplaceComment("workplaceComment");

    public final StringPath comment = createString("comment");

    public final NumberPath<Long> commentCode = createNumber("commentCode", Long.class);

    public final ArrayPath<byte[], Byte> commentUuid = createArray("commentUuid", byte[].class);

    public final BooleanPath deleteFlag = createBoolean("deleteFlag");

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final BooleanPath reportFlag = createBoolean("reportFlag");

    public final DateTimePath<java.sql.Timestamp> timestamp = createDateTime("timestamp", java.sql.Timestamp.class);

    public final NumberPath<Long> univCode = createNumber("univCode", Long.class);

    public final StringPath univName = createString("univName");

    public final NumberPath<Integer> upvote = createNumber("upvote", Integer.class);

    public final StringPath userId = createString("userId");

    public final StringPath userIp = createString("userIp");

    public final NumberPath<Long> workplaceCode = createNumber("workplaceCode", Long.class);

    public final StringPath workplaceName = createString("workplaceName");

    public QWorkplaceComment(String variable) {
        super(WorkplaceComment.class, forVariable(variable));
    }

    public QWorkplaceComment(Path<? extends WorkplaceComment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorkplaceComment(PathMetadata metadata) {
        super(WorkplaceComment.class, metadata);
    }

}

