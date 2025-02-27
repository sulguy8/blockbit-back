package com.sg.bbit.generate.mapper;

import java.util.List;

import com.sg.bbit.generate.vo.GenerateReqVO;
import com.sg.bbit.generate.vo.GenerateVO;

public interface GenerateMapper {
	GenerateVO selectDTI(GenerateReqVO generateReq);
	GenerateVO selectPAI(GenerateReqVO generateReq);
	List<GenerateVO> selectDCIList(GenerateReqVO generateReq);
	void createDBInfo();
}
