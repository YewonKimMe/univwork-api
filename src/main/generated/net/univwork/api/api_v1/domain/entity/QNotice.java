package net.univwork.api.api_v1.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotice is a Querydsl query type for Notice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotice extends EntityPathBase<Notice> {

    private static final long serialVersionUID = -663063251L;

    public static final QNotice notice = new QNotice("notice");

    public final StringPath author = createString("author");

    public final StringPath classification = createString("classification");

    public final NumberPath<Integer> commentNum = createNumber("commentNum", Integer.class);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> hits = createNumber("hits", Integer.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isFixed = createBoolean("isFixed");

    public final NumberPath<Long> no = createNumber("no", Long.class);

    public final ArrayPath<byte[], Byte> noticeId = createArray("noticeId", byte[].class);

    public final DateTimePath<java.sql.Timestamp> noticeTimestamp = createDateTime("noticeTimestamp", java.sql.Timestamp.class);

    public final StringPath title = createString("title");

    public QNotice(String variable) {
        super(Notice.class, forVariable(variable));
    }

    public QNotice(Path<? extends Notice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotice(PathMetadata metadata) {
        super(Notice.class, metadata);
    }

}

