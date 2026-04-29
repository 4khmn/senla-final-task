package com.project.velo.service.advertisement;

import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.request.AdvertisementFilterDto;
import com.project.velo.dto.response.advertisement.AdvertisementResponseDto;
import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.update.AdvertisementPromoteDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Category;
import com.project.velo.entity.SalesHistory;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.AdStatus;
import com.project.velo.entity.enums.Role;
import com.project.velo.exception.AdvertisementNotAvailableException;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.exception.ResourceAlreadyProcessedException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.AdvertisementMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.CategoryRepository;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import com.project.velo.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private AdvertisementMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SalesHistoryRepository salesHistoryRepository;

    @Mock
    private FileStorageService storageService;

    @InjectMocks
    private AdvertisementService advertisementService;


    @Test
    void create_WhenSuccess_WithFiles() {
        String username = "testUser";
        Long categoryId = 1L;
        AdvertisementCreateDto dto = new AdvertisementCreateDto("Bike", "Desc", BigDecimal.valueOf(100), categoryId);

        User seller = new User();
        seller.setUsername(username);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Mountain Bikes");

        Advertisement ad = new Advertisement();

        MockMultipartFile file1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", "data1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "img2.jpg", "image/jpeg", "data2".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        AdvertisementResponseDto expectedResponse = new AdvertisementResponseDto(
                1L, "Bike", "Desc", BigDecimal.valueOf(100), "ACTIVE", false,
                LocalDateTime.now(), null, "Mountain Bikes", "/path/1", List.of("/path/2")
        );

        given(mapper.toEntity(dto)).willReturn(ad);
        given(userRepository.findByUsername(username)).willReturn(Optional.of(seller));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        given(storageService.save(file1, "advertisements")).willReturn("/path/1");
        given(storageService.save(file2, "advertisements")).willReturn("/path/2");

        given(advertisementRepository.save(any(Advertisement.class))).willAnswer(inv -> inv.getArgument(0));
        given(mapper.toDto(any(Advertisement.class))).willReturn(expectedResponse);

        AdvertisementResponseDto result = advertisementService.create(dto, files, username);

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        assertNotNull(ad.getImages());
        assertEquals(2, ad.getImages().size());
        assertTrue(ad.getImages().get(0).isPrimary());
        assertFalse(ad.getImages().get(1).isPrimary());

        verify(storageService, times(2)).save(any(), eq("advertisements"));
        verify(advertisementRepository).save(ad);
    }

    @Test
    void create_ShouldThrowENFException_WhenUserNotFound() {
        String username = "ghost";
        AdvertisementCreateDto dto = new AdvertisementCreateDto("Bike", "Desc", BigDecimal.valueOf(100), 1L);

        given(mapper.toEntity(dto)).willReturn(new Advertisement());
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> advertisementService.create(dto, null, username)
        );

        verify(advertisementRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowENFException_WhenCategoryNotFound() {
        Long categoryId = 1L;
        String username = "user";
        AdvertisementCreateDto dto = new AdvertisementCreateDto("Bike", "Desc", BigDecimal.valueOf(100), categoryId);

        given(mapper.toEntity(dto)).willReturn(new Advertisement());
        given(userRepository.findByUsername(username)).willReturn(Optional.of(new User()));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> advertisementService.create(dto, null, username)
        );

        assertEquals("Категория с id " + dto.categoryId() + " не найдена", result.getMessage());
    }

    @Test
    void getById_ShouldReturnDto_Success() {
        Long adId = 1L;
        Advertisement ad = new Advertisement();
        ad.setId(adId);
        ad.setStatus(AdStatus.ACTIVE);

        AdvertisementResponseDto expectedDto = new AdvertisementResponseDto(
                adId, "Title", "Desc", BigDecimal.TEN, "ACTIVE",
                false, LocalDateTime.now(), null, "Category", "img", List.of()
        );

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(mapper.toDto(ad)).willReturn(expectedDto);

        AdvertisementResponseDto result = advertisementService.getById(adId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(advertisementRepository).findById(adId);
    }

    @Test
    void getById_ShouldThrowENFException_WhenNotFound() {
        Long adId = 1L;
        given(advertisementRepository.findById(adId)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> advertisementService.getById(adId));

        assertEquals("Объявления с id " + adId + " не найдено", result.getMessage());
    }

    @Test
    void getById_ShouldThrowAdvertisementNotAvailableException_WhenStatusIsNotActive() {
        Long adId = 1L;
        Advertisement ad = new Advertisement();
        ad.setId(adId);
        ad.setStatus(AdStatus.SOLD);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));

        AdvertisementNotAvailableException ex = assertThrows(AdvertisementNotAvailableException.class,
                () -> advertisementService.getById(adId));

        assertTrue(ex.getMessage().contains("не доступно"));
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getAll_ShouldReturnPageResponse_Success() {
        AdvertisementFilterDto filter = new AdvertisementFilterDto("Velo", 1L, new BigDecimal(0), new BigDecimal(100), null );
        int page = 0;
        int size = 5;
        Advertisement ad1 = new Advertisement();
        Advertisement ad2 = new Advertisement();
        List<Advertisement> entities = List.of(ad1, ad2);

        AdvertisementShortResponseDto dto1 = new AdvertisementShortResponseDto(
                1L, "Title 1", BigDecimal.TEN, "cat1", "img1", false, LocalDateTime.now(), "seller", BigDecimal.ONE);
        AdvertisementShortResponseDto dto2 = new AdvertisementShortResponseDto(
                2L, "Title 2", BigDecimal.valueOf(20), "cat2", "img2", true, LocalDateTime.now(), "seller", BigDecimal.ONE);

        given(advertisementRepository.findAllFiltered(filter, page, size))
                .willReturn(entities);
        given(advertisementRepository.countFiltered(filter)).willReturn(11L);

        given(mapper.toShortDto(ad1)).willReturn(dto1);
        given(mapper.toShortDto(ad2)).willReturn(dto2);

        PageResponse<AdvertisementShortResponseDto> result =
                advertisementService.getAll(filter, page, size);

        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(11L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(page, result.page());
        assertEquals(size, result.size());

        verify(advertisementRepository).findAllFiltered(filter, page, size);
        verify(advertisementRepository).countFiltered(filter);
        verify(mapper, times(2)).toShortDto(any());
    }

    @Test
    void getAll_ShouldReturnEmptyPage_WhenNoMatches() {
        AdvertisementFilterDto filter = new AdvertisementFilterDto("Velo", 1L ,new BigDecimal(0), new BigDecimal(100), null );

        int page = 0;
        int size = 10;

        given(advertisementRepository.findAllFiltered(filter, page, size))
                .willReturn(List.of());
        given(advertisementRepository.countFiltered(filter)).willReturn(0L);

        PageResponse<AdvertisementShortResponseDto> result =
                advertisementService.getAll(filter, page, size);

        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        verify(mapper, never()).toShortDto(any());
    }

    @Test
    void update_ShouldReturnDto_Success() {
        Long adId = 1L;
        String username = "ownerUser";
        AdvertisementUpdateDto dto = new AdvertisementUpdateDto(
                "New Title", "New Desc", BigDecimal.valueOf(200), 2L
        );

        MockMultipartFile file1 = new MockMultipartFile("file", "img1.jpg", "image/jpeg", "data1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "img2.jpg", "image/jpeg", "data2".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        User seller = new User();
        seller.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);
        ad.setImages(new ArrayList<>());

        Category newCategory = new Category();
        newCategory.setId(2L);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(categoryRepository.findById(2L)).willReturn(Optional.of(newCategory));
        given(mapper.toDto(ad)).willReturn(new AdvertisementResponseDto(
                adId, "New Title", "New Desc", BigDecimal.valueOf(200), "ACTIVE", false, null, null, "CatName", "/path/1", List.of("/path/2")
        ));
        given(storageService.save(file1, "advertisements")).willReturn("/path/1");
        given(storageService.save(file2, "advertisements")).willReturn("/path/2");


        AdvertisementResponseDto result = advertisementService.update(adId, dto, files, username);

        assertNotNull(result);
        verify(mapper).updateEntityFromDto(dto, ad);
        assertEquals(newCategory, ad.getCategory());
        assertEquals(2, ad.getImages().size());
        assertTrue(ad.getImages().get(0).isPrimary());

    }

    @Test
    void update_ShouldThrowNotEnoughRightsException_WhenUserIsNotOwner() {
        Long adId = 1L;
        String strangerName = "hackerUser";

        User actualOwner = new User();
        actualOwner.setUsername("ownerUser");

        Advertisement ad = new Advertisement();
        ad.setSeller(actualOwner);

        AdvertisementUpdateDto dto = new AdvertisementUpdateDto("Title", "Desc", BigDecimal.ONE, 1L);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));

        assertThrows(NotEnoughRightsException.class,
                () -> advertisementService.update(adId, dto, null, strangerName)
        );

        verify(mapper, never()).updateEntityFromDto(any(), any());
    }

    @Test
    void update_ShouldThrowENFException_WhenAdDoesNotExist() {
        Long adId = 1L;
        given(advertisementRepository.findById(adId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> advertisementService.update(adId, null, null, "anyUser")
        );
    }

    @Test
    void delete_Success_ByOwner() {
        Long adId = 1L;
        String username = "ownerUser";

        User seller = new User();
        seller.setUsername(username);
        seller.setRole(Role.ROLE_USER);

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);
        ad.setStatus(AdStatus.ACTIVE);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(seller));

        advertisementService.delete(adId, username);

        assertEquals(AdStatus.ARCHIVED, ad.getStatus());
        verify(advertisementRepository).findById(adId);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void delete_Success_ByAdmin() {
        Long adId = 1L;
        String adminName = "admin_boss";

        User owner = new User();
        owner.setUsername("regular_user");

        User admin = new User();
        admin.setUsername(adminName);
        admin.setRole(Role.ROLE_ADMIN);

        Advertisement ad = new Advertisement();
        ad.setSeller(owner);
        ad.setStatus(AdStatus.ACTIVE);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(adminName)).willReturn(Optional.of(admin));

        advertisementService.delete(adId, adminName);

        assertEquals(AdStatus.ARCHIVED, ad.getStatus());
    }

    @Test
    void delete_ShouldThrowNotEnoughRightsException_WhenNotOwnerAndNotAdmin() {
        Long adId = 1L;
        String strangerName = "stranger";

        User owner = new User();
        owner.setUsername("ownerUser");

        User stranger = new User();
        stranger.setUsername(strangerName);
        stranger.setRole(Role.ROLE_USER);

        Advertisement ad = new Advertisement();
        ad.setSeller(owner);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(strangerName)).willReturn(Optional.of(stranger));

        assertThrows(NotEnoughRightsException.class,
                () -> advertisementService.delete(adId, strangerName)
        );

        assertNotEquals(AdStatus.ARCHIVED, ad.getStatus());
    }

    @Test
    void delete_ShouldThrowENFException_WhenUserDoesNotExist() {
        String username = "ghost";
        given(advertisementRepository.findById(anyLong())).willReturn(Optional.of(new Advertisement()));
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> advertisementService.delete(1L, username)
        );

        assertEquals("Пользователя с username " + username + " не найдено", result.getMessage());
    }

    @Test
    void delete_ShouldThrowENFException_WhenAdvertisementDoesNotExist() {
        Long id = 1L;
        given(advertisementRepository.findById(id)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> advertisementService.delete(id, anyString())
        );

        assertEquals("Объявления с id " + id + " не найдено", result.getMessage());
    }

    @Test
    void processPurchase_Success() {
        Long adId = 1L;
        String buyerName = "buyerUser";
        String sellerName = "sellerUser";

        User seller = new User();
        seller.setUsername(sellerName);

        User buyer = new User();
        buyer.setUsername(buyerName);

        Advertisement ad = new Advertisement();
        ad.setId(adId);
        ad.setSeller(seller);
        ad.setPrice(BigDecimal.valueOf(1000));
        ad.setStatus(AdStatus.ACTIVE);
        ad.setTop(true);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(buyerName)).willReturn(Optional.of(buyer));

        advertisementService.processPurchase(adId, buyerName);

        assertEquals(AdStatus.SOLD, ad.getStatus());
        assertFalse(ad.isTop());

        verify(salesHistoryRepository).save(any(SalesHistory.class));
    }

    @Test
    void processPurchase_ShouldThrowValidationException_WhenBuyerIsSeller() {
        Long adId = 1L;
        String username = "sameUser";

        User user = new User();
        user.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setSeller(user);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> advertisementService.processPurchase(adId, username)
        );

        assertEquals("Нельзя купить свой собственный товар", ex.getMessage());
        verifyNoInteractions(salesHistoryRepository);
    }

    @Test
    void processPurchase_ShouldThrowResourceAlreadyProcessedException_WhenAdvertisementAlreadySold() {
        Long adId = 1L;
        String buyerName = "buyer";

        User seller = new User();
        seller.setUsername("seller");

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);
        ad.setStatus(AdStatus.SOLD);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(buyerName)).willReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyProcessedException.class,
                () -> advertisementService.processPurchase(adId, buyerName)
        );
        verify(salesHistoryRepository, never()).save(any(SalesHistory.class));
    }

    @Test
    void processPurchase_ShouldThrowENFException_WhenAdvertisementDoesNotExist() {
        given(advertisementRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> advertisementService.processPurchase(1L, "anyUser")
        );
        verify(salesHistoryRepository, never()).save(any(SalesHistory.class));
    }

    @Test
    void processPurchase_ShouldThrowENFException_WhenBuyerDoesNotExist() {
        Long adId = 1L;
        String username = "ghost";

        Advertisement ad = new Advertisement();

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> advertisementService.processPurchase(adId, username)
        );

        verify(salesHistoryRepository, never()).save(any(SalesHistory.class));
    }

    @Test
    void promote_Author_Success_FirstTime() {
        Long adId = 1L;
        String username = "seller";
        AdvertisementPromoteDto dto = new AdvertisementPromoteDto(7);

        User seller = new User();
        seller.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);
        ad.setStatus(AdStatus.ACTIVE);
        ad.setTopUntil(null);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(seller));
        advertisementService.promote(adId, dto, username);

        assertTrue(ad.isTop());
        assertNotNull(ad.getTopUntil());
        assertTrue(ad.getTopUntil().isAfter(LocalDateTime.now().plusDays(6)));
    }

    @Test
    void promote_Author_Success_Extension() {
        Long adId = 1L;
        String username = "seller";
        AdvertisementPromoteDto dto = new AdvertisementPromoteDto(3);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);

        Advertisement ad = new Advertisement();
        ad.setSeller(new User());
        ad.getSeller().setUsername(username);
        ad.setStatus(AdStatus.ACTIVE);
        ad.setTopUntil(futureDate);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(new User()));
        advertisementService.promote(adId, dto, username);

        assertEquals(futureDate.plusDays(3), ad.getTopUntil());
        assertTrue(ad.isTop());
    }

    @Test
    void promote_Admin_Success_FirstTime() {
        Long adId = 1L;
        String username = "adminUsername";
        AdvertisementPromoteDto dto = new AdvertisementPromoteDto(7);

        User admin = new User();
        admin.setUsername(username);
        admin.setRole(Role.ROLE_ADMIN);
        User seller = new User();
        seller.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);
        ad.setStatus(AdStatus.ACTIVE);
        ad.setTopUntil(null);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(admin));
        advertisementService.promote(adId, dto, username);

        assertTrue(ad.isTop());
        assertNotNull(ad.getTopUntil());
        assertTrue(ad.getTopUntil().isAfter(LocalDateTime.now().plusDays(6)));
    }

    @Test
    void promote_ShouldThrowNotEnoughRightsException_WhenNotOwner() {
        Long adId = 1L;
        Advertisement advertisement = new Advertisement();
        User seller = new User();
        seller.setUsername("seller");
        advertisement.setSeller(seller);
        given(advertisementRepository.findById(adId)).willReturn(Optional.of(advertisement));
        given(userRepository.findByUsername(any())).willReturn(Optional.of(seller));
        assertThrows(NotEnoughRightsException.class,
                () -> advertisementService.promote(adId, new AdvertisementPromoteDto(5), "hacker")
        );
    }

    @Test
    void promote_ShouldThrowNotENFException_WhenAdvertisementDoesNotExist() {
       Long adId = 1L;
        given(advertisementRepository.findById(adId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> advertisementService.promote(adId, new AdvertisementPromoteDto(5), "username")
        );
        verifyNoInteractions(userRepository);
    }

    @Test
    void promote_ShouldThrowNotENFException_WhenUserDoesNotExist() {
        Long adId = 1L;
        Advertisement advertisement = new Advertisement();
        given(advertisementRepository.findById(adId)).willReturn(Optional.of(advertisement));
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> advertisementService.promote(adId, new AdvertisementPromoteDto(5), "username")
        );
    }




    @Test
    void promote_ShouldThrowAdvertisementNotAvailableException_WhenStatusNotActive() {
        Long adId = 1L;
        User seller = new User();
        seller.setUsername("owner");

        Advertisement ad = new Advertisement();
        ad.setSeller(seller);
        ad.setStatus(AdStatus.SOLD);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(userRepository.findByUsername(any())).willReturn(Optional.of(seller));
        assertThrows(AdvertisementNotAvailableException.class,
                () -> advertisementService.promote(adId, new AdvertisementPromoteDto(1), "owner")
        );
    }

    @Test
    void findAdvertisementsByUsername_ShouldReturnPageResponse_Success() {
        String username = "velo_master";
        int page = 0;
        int size = 5;

        Advertisement ad = new Advertisement();
        ad.setTitle("Classic Bike");

        AdvertisementResponseDto dto = new AdvertisementResponseDto(
                1L, "Classic Bike", "Desc", BigDecimal.TEN, "ACTIVE",
                false, LocalDateTime.now(), null, "Bikes", "url", List.of()
        );

        given(userRepository.existsByUsername(username)).willReturn(true);
        given(advertisementRepository.findAllByUsername(username, page, size))
                .willReturn(List.of(ad));
        given(advertisementRepository.countByUsernameAndStatus(username, AdStatus.ACTIVE))
                .willReturn(7L);
        given(mapper.toDto(ad)).willReturn(dto);


        PageResponse<AdvertisementResponseDto> result =
                advertisementService.findAdvertisementsByUsername(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(7L, result.totalElements());

        assertEquals(2, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).existsByUsername(username);
        verify(advertisementRepository).findAllByUsername(username, page, size);
    }

    @Test
    void findAdvertisementsByUsername_ShouldThrowENFException_WhenUserDoesNotExist() {
        String username = "username";
        given(userRepository.existsByUsername(username)).willReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> advertisementService.findAdvertisementsByUsername(username, 0, 10)
        );

        assertTrue(ex.getMessage().contains(username));
        verify(advertisementRepository, never()).findAllByUsername(anyString(), anyInt(), anyInt());
    }

    @Test
    void getAllForAdmin_ShouldReturnPageResponse_Success() {
        int page = 0;
        int size = 10;
        List<Advertisement> entities = List.of(new Advertisement());
        AdvertisementResponseDto expectedDto = new AdvertisementResponseDto(
                1L, "Title", "Desc", BigDecimal.TEN, "ACTIVE",
                false, LocalDateTime.now(), null, "Category", "img", List.of()
        );
        when(advertisementRepository.findAllForAdmin(page, size)).thenReturn(entities);
        when(advertisementRepository.countAll()).thenReturn(25L);
        when(mapper.toDto(any(Advertisement.class))).thenReturn(expectedDto);

        PageResponse<AdvertisementResponseDto> result = advertisementService.getAllForAdmin(page, size);

        assertEquals(25L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(1, result.content().size());
        assertEquals(page, result.page());

        verify(advertisementRepository).findAllForAdmin(page, size);
        verify(advertisementRepository).countAll();
        verify(mapper, times(1)).toDto(any());
    }

    @Test
    void getAllForAdmin_ShouldReturnEmptyList_WhenPageIsOutOfBounds() {
        int page = 5;
        int size = 10;

        when(advertisementRepository.findAllForAdmin(page, size)).thenReturn(List.of());
        when(advertisementRepository.countAll()).thenReturn(25L);

        PageResponse<AdvertisementResponseDto> result = advertisementService.getAllForAdmin(page, size);

        assertTrue(result.content().isEmpty());
        assertEquals(3, result.totalPages());
        assertEquals(25L, result.totalElements());
    }
}
