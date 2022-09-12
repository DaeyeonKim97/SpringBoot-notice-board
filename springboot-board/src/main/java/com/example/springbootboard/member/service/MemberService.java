package com.example.springbootboard.member.service;

import com.example.springbootboard.member.dao.MemberMapper;
import com.example.springbootboard.member.dto.MemberDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PasswordEncoder passwordEncoder;

    private final MemberMapper mapper;

    public MemberService(PasswordEncoder passwordEncoder, MemberMapper mapper) {
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public boolean selectMemberById(String userId) {

        String result = mapper.selectMemberById(userId);

        return result != null? true : false;
    }

    @Transactional
    public void registMember(MemberDTO member) throws Exception{

        log.info("[MemberService] Insert Member : " + member);
        int result = mapper.insertMember(member);

        log.info("[MemberService] Insert result : " + ((result > 0) ? "회원가입 성공" : "회원가입 실패"));

        if(!(result > 0 )){
            throw new Exception("회원 가입에 실패하였습니다.");
        }
    }

    /* 회원 정보 수정용 메소드 */
    public void modifyMember(MemberDTO member) throws Exception {
        int result = mapper.updateMember(member);

        if(!(result > 0)) {
            throw new Exception("회원 정보 수정에 실패하셨습니다.");
        }
    }

    /* 회원 탈퇴용 메소드 */
    public void removeMember(MemberDTO member) throws Exception {
        int result = mapper.deleteMember(member);

        if(!(result > 0)) {
            throw new Exception("회원 탈퇴에 실패하셨습니다.");
        }
    }
}
