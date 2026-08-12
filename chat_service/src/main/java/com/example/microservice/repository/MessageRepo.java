package com.example.microservice.repository;

import com.example.microservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface MessageRepo extends JpaRepository<Message, Long> {
        @Query(value = """
                        SELECT DATE(m.created_at) AS message_date,
                               SUM(CASE WHEN gc.conversation_id IS NOT NULL THEN 1 ELSE 0 END) AS group_messages,
                               SUM(CASE WHEN gc.conversation_id IS NULL THEN 1 ELSE 0 END) AS private_messages,
                               COUNT(*) AS total_messages
                        FROM messages m
                        LEFT JOIN group_conversations gc
                               ON gc.conversation_id = m.conversation_id
                        WHERE m.created_at >= :startDate
                          AND m.created_at < :endDate
                          AND (m.is_deleted = 0 OR m.is_deleted IS NULL)
                        GROUP BY DATE(m.created_at)
                        ORDER BY message_date
                        """, nativeQuery = true)
        List<Object[]> findAdminMessagesTimeline(
                @Param("startDate") LocalDateTime startDate,
                @Param("endDate") LocalDateTime endDate);

        Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

        Message findMessageById(Long id);

        Optional<Message> findFirstByConversationIdOrderByCreatedAtDescIdDesc(Long conversationId);

        boolean existsByConversationIdAndSenderId(Long conversationId, Long senderId);

        List<Message> findByConversationIdAndIdIn(Long conversationId, List<Long> ids);

        @Query("""
                        select m from Message m
                        where m.conversation.id = :conversationId
                          and (m.mediaUrl is not null or m.fileName is not null)
                          and (m.isDeleted = false or m.isDeleted is null)
                        order by m.createdAt desc
                        """)
        List<Message> findMediaAndFilesByConversationId(@Param("conversationId") Long conversationId);

        Optional<Message> findFirstByConversationIdAndTypeAndContentContaining(Long conversationId, String type,
                        String content);

        @Query("""
                        select m from Message m
                        where m.conversation.id = :conversationId
                          and m.senderId <> :userId
                          and (:lastDeliveredMessageId is null or m.id > :lastDeliveredMessageId)
                        order by m.id asc
                        """)
        List<Message> findUndeliveredMessagesForUser(
                        @Param("conversationId") Long conversationId,
                        @Param("userId") Long userId,
                        @Param("lastDeliveredMessageId") Long lastDeliveredMessageId);

        @Query("""
                select count(m) from Message m
                where m.conversation.id = :conversationId
                  and m.senderId <> :userId
                  and (:lastSeenMessageId is null or m.id > :lastSeenMessageId)
                """)
        long countUnreadMessages(
                @Param("conversationId") Long conversationId,
                @Param("userId") Long userId,
                @Param("lastSeenMessageId") Long lastSeenMessageId);

        Page<Message> findByModerationStatusNotOrderByCreatedAtDesc(String status, Pageable pageable);

        Page<Message> findByModerationStatusOrderByCreatedAtDesc(String status, Pageable pageable);

        @Query(value = "SELECT m.sender_id, COUNT(*) as cnt FROM messages m WHERE m.moderation_status <> 'NONE' GROUP BY m.sender_id ORDER BY cnt DESC LIMIT :limit", nativeQuery = true)
        List<Object[]> findTopOffenders(@Param("limit") int limit);

        @Query(value = "SELECT gc.group_id, COUNT(*) as cnt FROM messages m JOIN conversations c ON m.conversation_id = c.conversation_id JOIN group_conversations gc ON gc.conversation_id = c.conversation_id WHERE m.moderation_status <> 'NONE' GROUP BY gc.group_id ORDER BY cnt DESC LIMIT :limit", nativeQuery = true)
        List<Object[]> findTopGroupsWithViolations(@Param("limit") int limit);

        @Query(value = """
                        SELECT
                          COUNT(*) AS total_messages,
                          COALESCE(SUM(CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN 1 ELSE 0 END), 0) AS total_violations,
                          COALESCE(SUM(CASE WHEN m.moderation_status = 'OFFENSIVE' THEN 1 ELSE 0 END), 0) AS offensive_messages,
                          COALESCE(SUM(CASE WHEN m.moderation_status = 'HATE' THEN 1 ELSE 0 END), 0) AS hate_messages,
                          COUNT(DISTINCT CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN gc.group_id END) AS groups_with_violations,
                          COUNT(DISTINCT CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN m.sender_id END) AS violating_members
                        FROM messages m
                        JOIN group_conversations gc ON gc.conversation_id = m.conversation_id
                        """, nativeQuery = true)
        Object[] findGroupModerationSummary();

        @Query(value = """
                        SELECT
                          grouped.group_id,
                          grouped.conversation_id,
                          grouped.total_messages,
                          grouped.offensive_messages,
                          grouped.hate_messages,
                          grouped.violating_members,
                          grouped.last_violation_at
                        FROM (
                          SELECT
                            gc.group_id,
                            m.conversation_id,
                            COUNT(*) AS total_messages,
                            COALESCE(SUM(CASE WHEN m.moderation_status = 'OFFENSIVE' THEN 1 ELSE 0 END), 0) AS offensive_messages,
                            COALESCE(SUM(CASE WHEN m.moderation_status = 'HATE' THEN 1 ELSE 0 END), 0) AS hate_messages,
                            COUNT(DISTINCT CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN m.sender_id END) AS violating_members,
                            MAX(CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN m.created_at ELSE NULL END) AS last_violation_at
                          FROM messages m
                          JOIN group_conversations gc ON gc.conversation_id = m.conversation_id
                          GROUP BY gc.group_id, m.conversation_id
                        ) grouped
                        WHERE grouped.offensive_messages + grouped.hate_messages > 0
                        ORDER BY grouped.hate_messages DESC, grouped.offensive_messages + grouped.hate_messages DESC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<Object[]> findGroupModerationRisks(@Param("limit") int limit);

        @Query(value = """
                        SELECT
                          grouped.group_id,
                          grouped.sender_id,
                          grouped.total_messages,
                          grouped.offensive_messages,
                          grouped.hate_messages,
                          grouped.last_violation_at
                        FROM (
                          SELECT
                            gc.group_id,
                            m.sender_id,
                            COUNT(*) AS total_messages,
                            COALESCE(SUM(CASE WHEN m.moderation_status = 'OFFENSIVE' THEN 1 ELSE 0 END), 0) AS offensive_messages,
                            COALESCE(SUM(CASE WHEN m.moderation_status = 'HATE' THEN 1 ELSE 0 END), 0) AS hate_messages,
                            MAX(CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN m.created_at ELSE NULL END) AS last_violation_at
                          FROM messages m
                          JOIN group_conversations gc ON gc.conversation_id = m.conversation_id
                          JOIN conversation_participants cp ON cp.conversation_id = gc.conversation_id AND cp.user_id = m.sender_id
                          WHERE cp.left_at IS NULL
                          GROUP BY gc.group_id, m.sender_id
                        ) grouped
                        WHERE grouped.offensive_messages + grouped.hate_messages > 0
                        ORDER BY grouped.hate_messages DESC, grouped.offensive_messages + grouped.hate_messages DESC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<Object[]> findMemberModerationRisks(@Param("limit") int limit);

        @Query(value = """
                        SELECT
                          grouped.group_id,
                          grouped.conversation_id,
                          grouped.total_messages,
                          grouped.offensive_messages,
                          grouped.hate_messages,
                          grouped.last_violation_at
                        FROM (
                          SELECT
                            gc.group_id,
                            m.conversation_id,
                            COUNT(*) AS total_messages,
                            COALESCE(SUM(CASE WHEN m.moderation_status = 'OFFENSIVE' THEN 1 ELSE 0 END), 0) AS offensive_messages,
                            COALESCE(SUM(CASE WHEN m.moderation_status = 'HATE' THEN 1 ELSE 0 END), 0) AS hate_messages,
                            MAX(CASE WHEN m.moderation_status IN ('OFFENSIVE', 'HATE') THEN m.created_at ELSE NULL END) AS last_violation_at
                          FROM messages m
                          JOIN group_conversations gc ON gc.conversation_id = m.conversation_id
                          JOIN conversation_participants cp ON cp.conversation_id = gc.conversation_id AND cp.user_id = m.sender_id
                          WHERE m.sender_id = :senderId AND cp.left_at IS NULL
                          GROUP BY gc.group_id, m.conversation_id
                        ) grouped
                        WHERE grouped.offensive_messages + grouped.hate_messages > 0
                        ORDER BY grouped.hate_messages DESC, grouped.offensive_messages + grouped.hate_messages DESC
                        """, nativeQuery = true)
        List<Object[]> findUserGroupModerationRisks(@Param("senderId") Long senderId);

        @Query(value = """
                        SELECT
                          DATE(m.created_at) AS day,
                          COALESCE(SUM(CASE WHEN m.moderation_status = 'OFFENSIVE' THEN 1 ELSE 0 END), 0) AS offensive_messages,
                          COALESCE(SUM(CASE WHEN m.moderation_status = 'HATE' THEN 1 ELSE 0 END), 0) AS hate_messages
                        FROM messages m
                        JOIN group_conversations gc ON gc.conversation_id = m.conversation_id
                        WHERE DATE(m.created_at) >= DATE_SUB(CURDATE(), INTERVAL 4 DAY)
                        GROUP BY DATE(m.created_at)
                        ORDER BY day ASC
                        """, nativeQuery = true)
        List<Object[]> findGroupModerationTrend();
}
