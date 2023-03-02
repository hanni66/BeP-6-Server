package com.google.bep.service;

import com.google.bep.domain.model.Account;
import com.google.bep.domain.model.Mission;
import com.google.bep.domain.model.UserMission;
import com.google.bep.domain.repository.AccountRepository;
import com.google.bep.domain.repository.MissionRepository;
import com.google.bep.domain.repository.UserCompleteRepository;
import com.google.bep.domain.repository.UserMissionRepository;
import com.google.bep.dto.ResponseMissionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final AccountRepository accountRepository;
    private final UserCompleteRepository userCompleteRepository;
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;

    public List<ResponseMissionDTO> getMissions(String email) {
        Account account = accountRepository.findByEmail(email).orElse(null);    // 로그인된 유저 정보 가져옴
        List<ResponseMissionDTO> missions = new ArrayList<>();

        // 로그인된 유저가 할당받은 mission이 없을 경우
        if(userMissionRepository.countByAccount_Id(account.getId()) == 0) {
            List<Long> ids = userCompleteRepository.getUserCompletesById(account.getId());  // 미션 id 랜덤으로 3개 가져옴

            for(int i = 0; i<3; i++) {  // 미션id로 미션 가져옴
                Mission mission = missionRepository.findById(ids.get(i)).orElse(null);
                ResponseMissionDTO missionDTO = mission.toDTO();
                missions.add(missionDTO);
            }
            for(int i = 0; i<3; i++) {  // UserMission에 데이터 저장
                userMissionRepository.save(UserMission.builder()
                        .account(account)
                        .mission(missionRepository.findById(ids.get(i)).orElse(null))
                        .build());
            }

        // 로그인된 유저가 할당받은 mission가 이미 있을 경우
        } else {
            List<UserMission> userMissions = userMissionRepository.findByAccount_Id(account.getId());   // userMission 객체 가져옴
            for(int i = 0; i<3; i++) {
                /* Lazy(지연로딩) -> Eager(즉시로딩)으로 바꿔야 list에 들어가짐
                Eager는 연관관계까지 즉시 함께 조회하는 반면에, Lazy는 연관관계의 데이터를 실질적으로 요구할 때 조회를 함.
                그래서 getMission을 써도 Mission의 실질적인 데이터는 가져오지 않은 상태 */
                // 오호,,, DTO에 값을 넣어서 DTO를 list에 넣으니 해결!
                Mission mission = userMissions.get(i).getMission();
                ResponseMissionDTO missionDTO = mission.toDTO();
                missions.add(missionDTO);
            }
        }
        return missions;
    }
}
