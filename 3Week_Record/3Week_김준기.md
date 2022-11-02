## **Title: [3Week] 김준기**

### **미션 요구사항 분석 & 체크리스트**

---

매 주 제공되는 미션 별 요구사항을 기반으로 기능에 대한 분석을 진행한 후, 아래와 같은 체크리스트를 작성합니다.

### **필수과제**

- [x]  관리자 회원
- [x]  관리자 페이지
- [x]  정산데이터 생성
- [x]  건별 정산처리, 전체 정산처리

### **추가과제**

- [ ]  정산데이터를 배치로 생성
- [x]  출금 신청기능
- [ ]  출금 처리기능

### **N주차 미션 요약**

---

**[접근 방법]**

- 강사님의 스프링 음원결제의 21강~34강까지를 따라하며 필수과제를 수행하였습니다.
- 이전 주차의 코드를 리펙토링하는 과정에서 예상 보다 시간이 많이 소요되어 3주차 미션의 경우 최소한의 기능만 수행할 수 있도록 구현하였습니다.
    - member, post, product 엔티티에 대한 dto를 추가시켜 controller 단에서는 dto객체만 사용할 수 있도록 수정해 주었습니다.
        - 2주차부터는 시간이 부족하여 구현하지 못했습니다.
    - dto엔티티를 생성하고 나서 끝나는 게 아닌, mapping 방법, dto사용 범위, dto 필드 등 생각 보다 생각할 부분이 많다는 것을 느겼습니다.

### DTO

- 사용 이유
    1. 필요없는 필드까지 전달하기 때문에 데이터 낭비가 생긴다.
    2. 보안적으로 중요한 필드까지 view에 노출되는 문제점이 있다.
    3. 엔티티의 데이터가 변질될 위험이 커진다.
- 코드
    
    ```java
    // PostController 클래스
    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        PostDto postDto = postService.getPostById(postId);
        model.addAttribute("post", postDto);
    
        return "post/detail";
    }
    
    // PostService 클래스
    public PostDto getPostById(Long postId) {
        Post post = getPost(postId);
        return PostMapper.INSTANCE.entityToPostDto(post); // mapstruct 매핑 Post -> PostDto
    }
    ```
    
    - Controller 단에 있는 모든 Post엔티티를 PostDto로 수정하였습니다.
- 추후 깊게 공부하여 블로그에 정리할 예정입니다.

**[특이사항]**

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다. 추후 리팩토링 시, 어떤 부분을 추가적으로 진행하고 싶은지에 대해 구체적으로 작성해주시기 바랍니다.

- 아쉬웠던 점
    - dto를 적용하고 많은 시간을 소요하게 되면서 미션에 뒤처진거 같아 아쉽습니다.
    - 막상 dto를 적용하였지만 맞게 적용한 것인지 의문이 듭니다. 이 부분은 추후 공부를 통해 해결될 거 같습니다.
    - 3주차 과제의 퀄리티에 신경쓰지 못한 점이 아쉽습니다.
- 리펙토링
    - 테스트 코드를 추가 작성할 예정입니다.
    - dto 추가 작업을 마저할 예정입니다.
    - 추가 과제를 구현할 예정입니다.
    - 강사님 정답 코드를 보며 도움이 될만 한 부분을 프로젝트에 적용시킬 예정입니다.