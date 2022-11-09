## **Title: [4Week] 김준기**

### **미션 요구사항 분석 & 체크리스트**

---

매 주 제공되는 미션 별 요구사항을 기반으로 기능에 대한 분석을 진행한 후, 아래와 같은 체크리스트를 작성합니다.

### **필수과제**

- [x]  JWT 로그인 구현
- [x]  로그인 한 회원의 정보 구현
- [x]  내 도서 리스트 구현
- [x]  내 도서 상세정보 구현
- [ ]  Srping Doc 으로 API 문서화

### **추가과제**

- [x]  엑세스 토큰 화이트리스트 구현

### **N주차 미션 요약**

---

**[접근 방법]**

- API 명세에 맞추기 위해서 3주차 강사님 프로젝트로 시작하였습니다.
- 스프링 부트 프로젝트인  **JWT**와 ****스프링 시큐리티 JWT 인증, REST API**** 강의 영상을 참고하였습니다.
- 개발 진행 순서는 아래와 같이 진행하였습니다.
    1. Member 기능
        - 시큐리티 적용
        - 예외처리 로직 분리
        - jwt 적용
        - api 명세서에 맞게 response Dto 생성
    2. myBook 기능
        - api 명세서에 맞게 response Dto 생성
    3. 화이스 리스트 적용
        - 강의 영상 그대로 따라함

### 로그인 기능

1. **시큐리티 새로 추가.** (강의 영상 참고)
2. **예외처리 로직 분리**
    - 예외 핸들러 로직을 `@RestControllerAdvice` 를 사용하여 분리시켜주었습니다.
    - **`GlobalControllerAdvice` 클래스**
        - 스프링이 직접 발생시키는 예외를 처리합니다.
            - ex) 로그인 데이터 유효성 체크 실패 → `MethodArgumentNotValidException`
            - ex) 로그인 데이터 형식이 다를 경우 → `HttpMessageNotReadableException`
            
        
        
        ```java
        @Slf4j
        @RestControllerAdvice
        public class GlobalControllerAdvice {
        		
            @ExceptionHandler(MethodArgumentNotValidException.class)
            public ResponseEntity<RsData> MethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
        				/* 생략 */
            }
            @ExceptionHandler(HttpMessageNotReadableException.class)
            public ResponseEntity<RsData> HttpMessageNotReadableExceptions(HttpMessageNotReadableException ex) {
                log.error("[HttpMessageNotReadableException] ex", ex);
        
                return ResponseEntity.badRequest().body(
                        RsData.of("F-HttpMessageNotReadableException", ExceptionType.UNHANDLED_EXCEPTION.getMessage()));
            }
        }
        ```
        
    
    - **`ApiMemberControllerAdvice`클래스**
        - Member 관련 예외를 처리합니다.
            - ex) 아이디가 틀린 경우 `MemberInvalidException(ExceptionType.*MEMBER_USERNAME_NOT_FOUND*)`
            - ex) 비밀번호가 틀린 경우`MemberInvalidException(ExceptionType.*MEMBER_PASSWORD_MISMATCH*)`
        
        
        ```java
        @Slf4j
        @RestControllerAdvice
        public class ApiMemberControllerAdvice {
        
            @ExceptionHandler(MemberInvalidException.class)
            public ResponseEntity<RsData> MemberInvalidExceptions(MemberInvalidException ex) {
                log.error("[MemberInvalidException] ex", ex);
        
                return ResponseEntity
                        .status(ex.getHttpStatus())
                        .body(RsData.of(String.valueOf(ex.getCode()), ex.getMessage()));
            }
        }
        ```
        
        <aside>
        💡 각각의 예외 상황마다 클래스를 생성해 줄 경우 파일이 양이 커지는 단점이 있다.  
            따라서 각 domain마다 하나의 예외 클래스만 만들어 준 다음 매개변수의 예외타입을 통해 구별할 수 있게 해주었다.
        **참고 자료**
        [https://github.com/woowacourse-teams/2022-ternoko/blob/develop/backend/src/main/java/com/woowacourse/ternoko/common/exception/ExceptionType.java](https://github.com/woowacourse-teams/2022-ternoko/blob/develop/backend/src/main/java/com/woowacourse/ternoko/common/exception/ExceptionType.java)
        
        </aside>
        

1. **JWT 추가.** (강의 영상 참고)

### myBook 기능

- db에서 데이터를 불러와서 api 명세서에 맞게 응답하도록 구현하였습니다.
- `/api/v1/myBooks/{myBookId}`
    - 현재 MyBook 엔티티를 통해 Post 엔티티에 접근할 수 있는 방법이 없기에 DB에서 가져오도록 하였습니다.
    - **service 단 로직**
        
        ```java
        public MyBookResponse getMyBookResponse(Long myBookId, Long memberId) {
        
                    /* 예외 처리 부분 생략*/

            /**
             *  도서(myBook)의 post 리스트를 가져와야 된다.
             *  1. 도서가 가진 Product의 PostKeyword를 가진 PostTag 리스트를 가져온다.
             *  2. PostTag 리스트에서 Post 리스트를 추출한 다음 응답 형태로 가공해준다.
             */
            List<PostResponse> bookChapters = getBookChapter(myBook); // api 명세서의 bookChapters
            ProductResponse productResponse = ProductResponse.of(myBook.getProduct(), bookChapters);

            return MyBookResponse.of(myBook, productResponse);
        }

        public List<PostResponse> getBookChapter(MyBook myBook) {
            List<PostTag> postTags = postTagRepository.findAllByPostKeyword(myBook.getProduct().getPostKeyword());
            return postTags.stream()
                    .map((postTag) -> PostResponse.of(postTag.getPost()))
                    .collect(toList());
        }
        ```
        
    - **ResponseDto**
        
        ```java
        public class MyBookResponse {
            private Long id;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
            private Long ownerId;
            private ProductResponse product;
        
            public static MyBookResponse of(MyBook myBook) {/*생략*/}
        }
        
        public class ProductResponse {
            private Long id;
            private LocalDateTime createDate;
            private LocalDateTime modifyDate;
            private Long authorId;
            private String authorName;
            private String subject;
            @JsonInclude(JsonInclude.Include.NON_NULL) // null 일 경우 데이터 안 넘김
            private List<PostResponse> bookChapters;
        
            public static ProductResponse of(Product product) {/*생략*/}
        }
        
        public class PostResponse {
            private Long id;
            private String subject;
            private String content;
            private String contentHtml;
        
            public static PostResponse of(Post post) {/*생략*/}
        }
        ```
        
        - MyBookResponse 객체를 생성하기 위해서는 연쇄적으로 다른 ResponseDto를 생성해줘야 하는 번거로움이 있습니다.
            
            → 좋은 방법인지 잘 모르겠습니다.
            
    

### 화이스 리스트 적용

- 강의 영상 참고

**[특이사항]**

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다. 추후 리팩토링 시, 어떤 부분을 추가적으로 진행하고 싶은지에 대해 구체적으로 작성해주시기 바랍니다.

- 아쉬웠던 점
    - Redis를 적용하면서 생각보다 많은 에러가 발생하였습니다. 잘 모르는 상태로 건들이다 보니 제대로 활용하지 못한 거 같습니다.
    - cache나 JWT 에 대한 테스트 케이스를 구현해주지 못한 점이 아쉽습니다.
    - TDD 방식으로 구현하고 싶었지만, 기능을 먼저 구현하고 Test case를 만들어 준 점이 아쉽습니다.
- 궁금한 점
    - Redis에 사용자 정의 객체를 저장할 경우, 강의 영상에서는 직접 Map으로 변환해주었지만 redis의 설정을 통해 해결이 되는지 궁금합니다.
- 리펙토링
    - 테스트 케이스를 더 만들어 줄 예정입니다.
    - redis에 사용자 정의 객체를 저장하는 방식을 개선할 예정입니다.
