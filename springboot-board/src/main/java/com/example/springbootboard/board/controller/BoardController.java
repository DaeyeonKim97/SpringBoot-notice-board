package com.example.springbootboard.board.controller;

import com.example.springbootboard.board.dto.BoardDTO;
import com.example.springbootboard.board.dto.ReplyDTO;
import com.example.springbootboard.board.paging.Pagenation;
import com.example.springbootboard.board.paging.SelectCriteria;
import com.example.springbootboard.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping(value = "/list")
    public ModelAndView boardList(HttpServletRequest request, ModelAndView mv) {

        String currentPage = request.getParameter("currentPage");
        int pageNo = 1;

        if(currentPage != null && !"".equals(currentPage)) {
            pageNo = Integer.parseInt(currentPage);
        }

        String searchCondition = request.getParameter("searchCondition");
        String searchValue = request.getParameter("searchValue");

        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("searchCondition", searchCondition);
        searchMap.put("searchValue", searchValue);

        int totalCount = boardService.selectTotalCount(searchMap);

        int limit = 10;

        int buttonAmount = 5;

        SelectCriteria selectCriteria = null;

        if(searchCondition != null && !"".equals(searchCondition)) {
            selectCriteria = Pagenation.getSelectCriteria(pageNo, totalCount, limit, buttonAmount, searchCondition, searchValue);
        } else {
            selectCriteria = Pagenation.getSelectCriteria(pageNo, totalCount, limit, buttonAmount);
        }

        List<BoardDTO> boardList = boardService.selectBoardList(selectCriteria);

        mv.addObject("boardList", boardList);
        mv.addObject("selectCriteria", selectCriteria);
        mv.setViewName("content/board/boardList");

        return mv;
    }

    @GetMapping("/detail")
    public String selectBoardDetail(HttpServletRequest request, Model model) {

        Long no = Long.valueOf(request.getParameter("no"));
        BoardDTO boardDetail = boardService.selectBoardDetail(no);

        model.addAttribute("board", boardDetail);

        /* 댓글 작성 완료 후 추가할 것 */
        List<ReplyDTO> replyList = boardService.selectAllReplyList(no);
        model.addAttribute("replyList", replyList);

        return "content/board/boardDetail";
    }

    @PostMapping("/registReply")
    public ResponseEntity<List<ReplyDTO>> registReply(@RequestBody ReplyDTO registReply) throws Exception {

        List<ReplyDTO> replyList = boardService.registReply(registReply);

        return ResponseEntity.ok(replyList);
    }

    @DeleteMapping("/removeReply")
    public ResponseEntity<List<ReplyDTO>> removeReply(@RequestBody ReplyDTO removeReply) throws Exception {

        List<ReplyDTO> replyList = boardService.removeReply(removeReply);

        return ResponseEntity.ok(replyList);
    }

    @GetMapping("/regist")
    public String goRegister() {
        return "content/board/boardRegist";
    }

    @PostMapping("/regist")
    public String registBoard(@ModelAttribute BoardDTO board, RedirectAttributes rttr) throws Exception {

        boardService.registBoard(board);

        rttr.addFlashAttribute("message", "게시글 등록에 성공하셨습니다!");

        return "redirect:/board/list";
    }


}

