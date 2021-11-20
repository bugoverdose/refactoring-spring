# Refactoring Spring
- 동일한 서비스 로직을 다양한 방식으로 리팩토링해보는 예제 코드 

- 프로젝트 실행 방법

1. 로컬에 git clone 
2. IntelliJ로 해당 프로젝트의 build.gradle 파일을 선택하여 프로젝트를 열기
3. 실행하고 싶은 컨트롤러를 `제외한` 나머지 2개의 컨트롤러 및 관련 서비스들의 빈 등록 관련 어노테이션을 주석처리시켜서 서버 실행
    - VerboseService 서비스 클래스를 주입받는 VerboseController : 서비스 파일과 컨트롤러 파일
    - RefactoredService 인터페이스를 주입받는 RefactoredController : 서비스 구현체 파일 2개와 컨트롤러 파일
    - EventDrivenService 인터페이스를 주입받는 EventDrivenController : 서비스 구현체 파일과 컨트롤러 파일

- cf) 2번 코드보다 3번 코드가 우월한 것은 아님. 상황에 따라 적절히 선택해야 함.

## 프로젝트 개요

- 세상에는 두 부류의 사람이 있습니다. 

- 너드인 사람과 그렇지 않은 사람.

- 해당 프로젝트는 각 사용자의 너드 점수를 토대로 각 사용자들을 일반인 혹은 너드로 분류하고자 합니다.

## API 구조

[GET] /refactoring/me
- HTTP HEADER의 USER-ID값에 해당되는 사용자 정보가 존재하면 `해당 사용자 정보 조회`
- 해당 정보가 없으면 에러

[GET] /refactoring/users/{statusInput}
- statusInput 값이 NERD면 `모든 너드들의 사용자 정보 조회`
- statusInput 값이 NORMAL면 `모든 일반인들의 사용자 정보 조회`
- 그 외의 값이 들어온 경우 에러

[POST] /refactoring/user
- RequestBody의 name에 해당되는 `사용자 정보 생성`
- 동명이인이 이미 존재할 경우 에러
- 생성된 사용자의 이름을 기준으로 로그 남기기

[PUT] /refactoring/user
- RequestBody의 userId에 해당되는 `사용자의 nerdPoint를 additionalPoint만큼 더하거나 차감`
- 갱신된 nerdPoint를 기준으로 사용자의 Status를 NERD 혹은 NORMAL로 변경
- 존재하지 않는 사용자면 에러. 추가하려는 값이 0이면 에러.
- 사용자의 너드 여부(Status)가 변경된 경우 로그 남기기

# 서비스1 : verbose
- 기본적으로 VerboseService 서비스 하나에 모든 로직이 담긴 경우
- 할 수 있는 한 최대한 더럽게 작성된 코드

# 서비스2 : 인터페이스와 구현체 활용
- RefactoredService 인터페이스에서 API 명세 정의
- RefactoredServicePrimaryImpl 구현체에서 방어로직 위주로 실행하고 다음 구현체 호출 여부 결정
- RefactoredServiceImpl 구현체에서 실제로 DB 변동, 로깅 작업 등 세부 로직 전부 진행
- cf) Impl 구현체의 개수는 알아서 판단. 다만, 2개를 넘어가면 오히려 가독성이 감소할 것으로 보임.
- TMI) 동일한 요청에 대해 이전에 DB에서 가져온 데이터는 JPA pool에 담기게 되고 이후 다른 코드에서 동일한 방식으로 해당 데이터를 요청하는 경우, JPA pool에 캐슁된 데이터를 그대로 사용하는 것으로 보임.
  
## 서비스2 부록 : command와 model 응용
- model 클래스에서는 companion object의 of 함수를 활용하여 Entity를 받아 해당 Model의 형식으로 변환 가능
- command에서는 toEntity 함수를 통해 해당 command로 생성된 인스턴스 정보를 토대로 Entity 데이터 생성 가능

# 서비스3 : EDA 
- 정리: EventListener들을 특정 이벤트에 대해 대기시켜놓고, 서비스 로직 실행 도중에 해당 이벤트들을 publish시킴으로써 EventListener를 발동시키는 구조
  - 로깅 외에도 다른 도메인의 서비스를 호출하거나, 다른 DB 변동 작업을 수행하는 등 다양한 작업 실행 가능. 
  - 비교) Event Sourcing은 DB에 데이터의 최종 결과를 저장해놓는 CRUD와 달리 데이터의 변화 과정(event)과 중간 합계(snapshot)를 저장해놓는 기법
- AOP와 커스텀 어노테이션을 활용하여 EventPublishing 과정 구현 (config의 PublishEvent와 EventPublisher 참고)
- 구현체가 1개가 된 이유는 구현체를 둘로 나누는 것이 오히려 가독성을 해치고 있어서, 구현체가 개수와 무관하게 EDA는 구현 가능
- cf) 반환값을 event로 설정하는 방식의 경우 AOP를 활용한 event publishing 구현이 용이하지만 컨트롤러 단에서 별도의 작업 필요. 다른 방법들로도 구현 가능.
     

## 서비스3 부록 
- getByStatus: query 클래스를 활용하여 init에서 DB가 불필요한 방어로직을 사전에 실행 & 더러운 로직은 함수화하여 서비스에서 그대로 실행
- Destructuring Declarations : 자스의 Object Destructuring 같은 것이 kotlin에도 존재함.
  - ex) `val (user, statusChanged) = event`
    
---

## 숙제

- createUser 함수가 개별적으로 호출될 때는 AOP가 동작한다.
- EventServiceImpl에서 호출되는 createUser 함수는 AOP를 동작시키지 않는다.
- 그 이유에 대해 서술하시오.
