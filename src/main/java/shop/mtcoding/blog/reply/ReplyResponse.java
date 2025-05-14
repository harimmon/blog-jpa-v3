package shop.mtcoding.blog.reply;

import lombok.Data;

public class ReplyResponse {

    @Data
    public static class DTO {
        private Integer id;
        private String content;
        private Integer user;
        private Integer board;
        private String createdAt;

        public DTO(Reply reply) {
            this.id = reply.getId();
            this.content = reply.getContent();
            this.user = reply.getUser().getId();
            this.board = reply.getBoard().getId();
            this.createdAt = reply.getCreatedAt().toString();
        }
    }
}
