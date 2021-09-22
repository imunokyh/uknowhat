package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.ubusiness.question.domain.RoomQuestion;
import kr.or.uknowhat.api.ubusiness.question.repositories.RoomQuestionRepository;
import kr.or.uknowhat.api.ubusiness.question.repositories.RoomRepository;
import kr.or.uknowhat.api.ubusiness.question.service.RoomQuestionService;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionResMapping;
import kr.or.uknowhat.api.ubusiness.question.vo.RoomQuestionVo;
import kr.or.uknowhat.api.ubusiness.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class RoomQuestionServiceImpl implements RoomQuestionService {

	@Autowired
	private RoomQuestionRepository roomQuestionRepo;
	
	@Override
	public List<RoomQuestionResMapping> listQuestion(Long roomId) {
		return roomQuestionRepo.findAllByRoomId(roomId);	
	}
	
	@Override
	public List<RoomQuestionResMapping> readQuestion(String roomNumber) {
		Optional<Long> optionalRoomId = roomQuestionRepo.findRoomIdByRoomNumber(roomNumber);
		if (optionalRoomId.isPresent()) {
			return roomQuestionRepo.findAllByRoomId(optionalRoomId.get());
		}
		
		return null;
	}

	@Override
	public RoomQuestion getQuestion(Long id) {
		RoomQuestion rq = null;
		
		Optional<RoomQuestion> optionalRoom = roomQuestionRepo.findById(id);
		if (optionalRoom.isPresent()) {
			rq = optionalRoom.get();
		}
		
		return rq;
	}

	@Override
	public RoomQuestionVo insertQuestion(RoomQuestionVo rqVo) {
		
		RoomQuestion rq = RoomQuestion.ofRoomQuestionVo(rqVo);
		rq.setUserId(SecurityUtil.getUserId());
		rq = roomQuestionRepo.save(rq);
		RoomQuestionVo rqVoRet = RoomQuestionVo.ofRoomQuestion(rq);
		return rqVoRet;
	}

	@Override
	public RoomQuestionVo updateQuestion(RoomQuestionVo rqVo) {
		Optional<RoomQuestion> optionalRq = roomQuestionRepo.findById(rqVo.getId());
		if (optionalRq.isPresent()) {
			RoomQuestion rq = optionalRq.get();
			
			rq.setQuestionScore(rqVo.getQuestionScore());
			rq.setQuestionTime(rqVo.getQuestionTime());
			rq.setQuestionOrder(rqVo.getQuestionOrder());
			
			rq =  roomQuestionRepo.save(rq);
			RoomQuestionVo rqVoRet = RoomQuestionVo.ofRoomQuestion(rq);
			return rqVoRet;
		}
		return null;
	}

	@Override
	public void deleteQuestion(Long id) {
		Optional<RoomQuestion> optionalRoom = roomQuestionRepo.findById(id);
		if (optionalRoom.isPresent()) {
			roomQuestionRepo.delete(optionalRoom.get());
		}
	}

	@Override
	public RoomQuestionResMapping readOneQuestion(Long id) {
		return roomQuestionRepo.findOneById(id);
	}
	
}
