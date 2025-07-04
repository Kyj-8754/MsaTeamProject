package com.msa.kyj_prj.match.log;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MatchLogDAO {
    // 특정 매치의 로그 전체 조회
    List<MatchLog> selectLogsByMatchId(@Param("match_id") Long match_id);

    // 로그 등록
    int insertMatchLog(MatchLog log);

    // 로그 수정
    int updateMatchLog(MatchLog log);

    // 로그 삭제
    int deleteMatchLog(@Param("log_id") Long log_id);

}
