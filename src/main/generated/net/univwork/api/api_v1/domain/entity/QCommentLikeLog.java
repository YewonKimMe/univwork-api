package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentLikeLog is a Querydsl query type for CommentLikeLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentLikeLog extends EntityPathBase<CommentLikeLog> {

    private static final long serialVersionUID = 487131683L;

    public static final QCommentLikeLog commentLikeLog = new QCommentLikeLog("commentLikeLog");

    public final NumberPath<Long> commentCode = createNumber("commentCode", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public QCommentLikeLog(String variable) {
        super(CommentLikeLog.class, forVariable(variable));
    }

    public QCommentLikeLog(Path<? extends CommentLikeLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentLikeLog(PathMetadata metadata) {
        super(CommentLikeLog.class, metadata);
    }

}

