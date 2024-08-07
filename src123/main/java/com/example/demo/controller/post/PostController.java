package com.example.demo.controller.post;

import com.example.demo.domain.post.Posts;
import com.example.demo.dto.post.PostRequestDto;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Posts> createPost(@RequestHeader("accessToken") String token,
                                            @RequestBody PostRequestDto postRequestDto) {
        String email = jwtTokenProvider.getEmail(token.replace("Bearer ", ""));
        Posts createdPost = postService.createPost(email, postRequestDto);
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPost(@PathVariable Long postId, @RequestHeader("accessToken") String token) {
        String email = jwtTokenProvider.getEmail(token.replace("Bearer ", ""));
        Posts post = postService.getPost(postId);
        boolean isAuthor = post.getUser().getEmail().equals(email);

        return ResponseEntity.ok(new Object() {
            public final Posts postDetails = post;
            public final boolean author = isAuthor;
        });
    }

    @GetMapping("/user")
    public ResponseEntity<List<Posts>> getUserPosts(@RequestHeader("accessToken") String token) {
        String email = jwtTokenProvider.getEmail(token.replace("Bearer ", ""));
        List<Posts> userPosts = postService.getPostsByUser(email);
        return ResponseEntity.ok(userPosts);
    }
    @GetMapping("/university/{universityId}")
    public ResponseEntity<List<Posts>> getPostsByUniversity(@PathVariable Long universityId) {
        List<Posts> posts = postService.getPostsByUniversity(universityId);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/university/{universityId}/enddate")
    public ResponseEntity<Posts> getPostWithClosestDeadline(@PathVariable Long universityId) {
        Optional<Posts> post = postService.getClosestDeadlinePostByUniversity(universityId);
        Posts emptyPost = new Posts(); // 빈 Posts 객체 생성
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(emptyPost));
    }

    @GetMapping
    public ResponseEntity<List<Posts>> getAllPosts() {
        List<Posts> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Posts> updatePost(@RequestHeader("accessToken") String token,
                                            @PathVariable Long postId,
                                            @RequestBody PostRequestDto postRequestDto) {
        String email = jwtTokenProvider.getEmail(token.replace("Bearer ", ""));
        Posts updatedPost = postService.updatePost(postId, postRequestDto, email);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@RequestHeader("accessToken") String token,
                                           @PathVariable Long postId) {
        String email = jwtTokenProvider.getEmail(token.replace("Bearer ", ""));
        postService.deletePost(postId, email);
        return ResponseEntity.noContent().build();
    }
}
