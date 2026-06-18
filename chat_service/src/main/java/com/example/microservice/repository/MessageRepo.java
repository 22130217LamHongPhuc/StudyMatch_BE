package com.example.microservice.repository;

import com.example.microservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message, Long> {
        Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

        Message findMessageById(Long id);

        Optional<Message> findFirstByConversationIdOrderByCreatedAtDescIdDesc(Long conversationId);

        boolean existsByConversationIdAndSenderId(Long conversationId, Long senderId);

        List<Message> findByConversationIdAndIdIn(Long conversationId, List<Long> ids);

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
                          WHERE m.sender_id = :senderId
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
