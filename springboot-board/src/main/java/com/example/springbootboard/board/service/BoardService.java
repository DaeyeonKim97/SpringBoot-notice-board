package com.example.springbootboard.board.service;

import com.example.springbootboard.board.dao.BoardMapper;
import com.example.springbootboard.board.dto.AttachmentDTO;
import com.example.springbootboard.board.dto.BoardDTO;
import com.example.springbootboard.board.dto.ReplyDTO;
import com.example.springbootboard.board.paging.SelectCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BoardService {

    private final BoardMapper mapper;

    public BoardService(BoardMapper mapper) {
        this.mapper = mapper;
    }

    public int selectTotalCount(Map<String, String> searchMap) {

        int result = mapper.selectTotalCount(searchMap);

        return result;
    }

    public List<BoardDTO> selectBoardList(SelectCriteria selectCriteria) {

        List<BoardDTO> boardList = mapper.selectBoardList(selectCriteria);

        return boardList;
    }

    @Transactional
    public BoardDTO selectBoardDetail(Long no) {
        BoardDTO boardDetail = null;

        int result = mapper.incrementBoardCount(no);

        if(result > 0){
            boardDetail = mapper.selectBoardDetail(no);
        }

        return boardDetail;
    }

    public List<ReplyDTO> selectAllReplyList(Long boardNo) {
        List<ReplyDTO> replyList = null;

        replyList = mapper.selectReplyList(boardNo);

        return replyList;
    }


    @Transactional
    public List<ReplyDTO> registReply(ReplyDTO registReply) throws Exception {
        List<ReplyDTO> replyList = null;

        int result = mapper.insertReply(registReply);

        if(result > 0) {
            replyList = mapper.selectReplyList(registReply.getRefBoardNo());
        } else {
            throw new Exception("댓글 등록에 실패하셨습니다.");
        }

        return replyList;
    }

    @Transactional
    public List<ReplyDTO> removeReply(ReplyDTO removeReply) throws Exception {
        List<ReplyDTO> replyList = null;

        int result = mapper.deleteReply(removeReply.getNo());

        if(result > 0) {
            replyList = mapper.selectReplyList(removeReply.getRefBoardNo());
        } else {
            throw new Exception("댓글 삭제에 실패하셨습니다.");
        }

        return replyList;
    }

    @Transactional
    public void registBoard(BoardDTO board) throws Exception {
        int result = mapper.insertBoard(board);

        if(!(result > 0)) {
            throw new Exception("게시글 등록에 실패하셨습니다.");
        }
    }

    public List<BoardDTO> selectAllThumbnailList() {
        List<BoardDTO> thumbnailList = mapper.selectAllThumbnailList();

        return thumbnailList;
    }

    @Transactional
    public void registThumbnail(BoardDTO thumbnail) throws Exception {

        int result = 0;

        /* 먼저 board 테이블부터 insert 한다. */
        int boardResult = mapper.insertThumbnailContent(thumbnail);

        /* Attachment 리스트를 불러온다. */
        List<AttachmentDTO> attachmentList = thumbnail.getAttachmentList();

        /* fileList에 boardNo값을 넣는다. */
        for(int i = 0; i < attachmentList.size(); i++) {
            attachmentList.get(i).setRefBoardNo(thumbnail.getNo());
        }

        /* Attachment 테이블에 list size만큼 insert 한다. */
        int attachmentResult = 0;
        for(int i = 0; i < attachmentList.size(); i++) {
            attachmentResult += mapper.insertAttachment(attachmentList.get(i));
        }

        /* 게시글 추가 및 첨부파일 갯수 만큼 첨부파일 내용 insert에 실패 시 예외 발생 */
        if(!(boardResult > 0 && attachmentResult == attachmentList.size())) {
            throw new Exception("사진 게시판 등록에 실패하셨습니다.");
        }
    }

    public BoardDTO selectThumbnailDetail(Long no) {
        BoardDTO thumbnailDetail = null;

        int result = mapper.incrementBoardCount(no);

        if(result > 0) {
            thumbnailDetail = mapper.selectThumbnailDetail(no);
        }

        return thumbnailDetail;
    }
}