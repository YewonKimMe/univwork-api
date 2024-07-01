package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReportedComment is a Querydsl query type for ReportedComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportedComment extends EntityPathBase<ReportedComment> {

    private static final long serialVersionUID = 1405849431L;

    public static final QReportedComment reportedComment = new QReportedComment("reportedComment");

    public final StringPath comment = createString("comment");

    public final ArrayPath<byte[], Byte> commentUuid = createArray("commentUuid", byte[].class);

    public final BooleanPath inProgress = createBoolean("inProgress");

    public final NumberPath<Long> No = createNumber("No", Long.class);

    public final StringPath reason = createString("reason");

    public final StringPath reportedUserId = createString("reportedUserId");

    public final StringPath reportedUserIp = createString("reportedUserIp");

    public final StringPath reportUserId = createString("reportUserId");

    public final StringPath reportUserIp = createString("reportUserIp");

    public final DateTimePath<java.sql.Timestamp> time = createDateTime("time", java.sql.Timestamp.class);

    public QReportedComment(String variable) {
        super(ReportedComment.class, forVariable(variable));
    }

    public QReportedComment(Path<? extends ReportedComment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportedComment(PathMetadata metadata) {
        super(ReportedComment.class, metadata);
    }

}

