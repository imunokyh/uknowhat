package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.framework.exception.BizException;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.domain.Play;
import kr.or.uknowhat.api.ubusiness.question.domain.PlayAnswer;
import kr.or.uknowhat.api.ubusiness.question.domain.PlayStat;
import kr.or.uknowhat.api.ubusiness.question.repositories.PlayAnswerRepository;
import kr.or.uknowhat.api.ubusiness.question.repositories.PlayRepository;
import kr.or.uknowhat.api.ubusiness.question.repositories.PlayStatRepository;
import kr.or.uknowhat.api.ubusiness.question.repositories.RoomQuestionRepository;
import kr.or.uknowhat.api.ubusiness.question.service.PlayService;
import kr.or.uknowhat.api.ubusiness.question.vo.PlayAnswerVo;
import kr.or.uknowhat.api.ubusiness.question.vo.PlayStatWinnerMapping;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PlayServiceImpl implements PlayService{

	
	@Autowired
	private RoomQuestionRepository roomQuestionRepo;
	
	@Autowired
	private PlayRepository playRepo;
	
	@Autowired
	private PlayAnswerRepository playAnswerRepo;
	
	@Autowired
	private PlayStatRepository playStatRepo;
	
	
	@Override
	public void readProb(Long roomId, String roomNumber) {
		final String _roomNumber = "1234";
		List<RoomQuestionResMapping> roomQuestionList = roomQuestionRepo.findAllByRoomId(roomId);
		if (roomQuestionList == null) {
			throw new BizException(ErrorCode.WRONG_REQUEST,ErrorCode.WRONG_REQUEST.getMessage() );
		}
		playRepo.deleteAllByRoomId(roomId); // 해당방으로 생성된 것 먼저 삭제
		log.info("list count:" + roomQuestionList.size());
		roomQuestionList.forEach(rq->{
			
			Play play = Play.ofRoomQuestionResMapping(rq);
			
			play.setRoomNumber(_roomNumber);
			playRepo.save(play);
		});
	}
	
	@Override
	public void readNext(String roomNumber) {
		roomNumber = "1234";
		List<String> procCodeList = new ArrayList<>();
		procCodeList.add("N"); // 미처리
		procCodeList.add("P"); // 처리
		Optional<Play> oNextPlay = playRepo.findTop1ByRoomNumberAndProcCodeOrderByQuestionNumberAsc(roomNumber, "N");
		Optional<Play> oCurPlay = playRepo.findTop1ByRoomNumberAndProcCodeOrderByQuestionNumberAsc(roomNumber, "C");
		//Play play = playRepo.findTop1ByProcCodeNotInOrderByQuestionNumberAsc(procCodeList);
		
		if (oCurPlay.isPresent() == false && oNextPlay.isPresent() == false) {
			log.info("======================");
			log.info("all problem is solved");
			log.info("======================");
			return;
		}
		if(oCurPlay.isPresent()) {
			Play curPlay = oCurPlay.get();
			curPlay.setProcCode("P"); // 현재진행
			playRepo.save(curPlay);
		}
		if(oNextPlay.isPresent()) {
			Play nextPlay = oNextPlay.get();
			nextPlay.setProcCode("C");
			playRepo.save(nextPlay);
		}
	}
	
	@Override
	public void checkAnswer(String roomNumber) {
		final String _roomNumber = "1234";
		Optional<Play> oCurPlay = playRepo.findTop1ByRoomNumberAndProcCodeOrderByQuestionNumberAsc(_roomNumber, "C");
		//Play play = playRepo.findTop1ByProcCodeNotInOrderByQuestionNumberAsc(procCodeList);
		
		if(!oCurPlay.isPresent()) {
			throw new BizException(ErrorCode.WRONG_REQUEST,ErrorCode.WRONG_REQUEST.getMessage() );
		}
		Play curPlay = oCurPlay.get();
		String questionAnswer = curPlay.getQuestionAnswer(); // 문제정답
		Long questionNumber = curPlay.getQuestionNumber();
		Long score = curPlay.getQuestionScore();		
		List<PlayAnswer> answerList = playAnswerRepo.findAllByRoomNumberAndQuestionNumber(_roomNumber, questionNumber);
		log.info(questionNumber + "");
		log.info(answerList.toString());
		answerList.forEach(answer->{
			PlayStat playStat = new PlayStat();
			playStat.setRoomNumber(_roomNumber);
			playStat.setParticipantName(answer.getParticipantName());
			playStat.setQuestionNumber(questionNumber);
			playStat.setQuestionAnswer(questionAnswer);
			playStat.setAnswerText(answer.getAnswerText());
			if(questionAnswer.equalsIgnoreCase(answer.getAnswerText())) {
				playStat.setCorrectYn("Y");
				playStat.setScore(score);
			}else {
				playStat.setCorrectYn("N");
				playStat.setScore(0L);
			}
			playStatRepo.save(playStat);
			
		});
	}
	
	// TODO send result of check
	
	@Override
	public void writeAnswer(PlayAnswerVo playAnswerVo) {
		PlayAnswer playAnswer = PlayAnswer.ofPlayAnswerVo(playAnswerVo);
		playAnswerRepo.save(playAnswer);
	}
	
	// 최종순위 매기기
	@Override
	public void whoWinQuiz(String roomNumber) {
		
		// sum result and chech the winner
		// 
		List<PlayStatWinnerMapping> playStatWinnerMappingList = playStatRepo.findAllTop3Ranker(roomNumber);
		playStatWinnerMappingList.forEach(playStatWinner->{
			log.info(playStatWinner.getRanking()+"/" + playStatWinner.getParticipantName() + "/" + playStatWinner.getTotalScore());
		});
		String winnerName = ((PlayStatWinnerMapping)playStatWinnerMappingList.get(0)).getParticipantName();
		log.info(winnerName);
	}
	
	
}
