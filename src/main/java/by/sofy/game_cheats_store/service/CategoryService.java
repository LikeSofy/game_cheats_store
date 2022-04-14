package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.CategoryCreationEditRequest;
import by.sofy.game_cheats_store.dto.GameCreationEditRequest;
import by.sofy.game_cheats_store.entity.Category;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidationService validationService;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public CategoryCreationEditRequest findRequestById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found category by id: %s", id)));
        CategoryCreationEditRequest request = new CategoryCreationEditRequest();
        request.setName(category.getName());

        return request;
    }

    public void create(CategoryCreationEditRequest request){
        validationService.checkValidation(request);
        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    public void edit(Long id, CategoryCreationEditRequest request){
        validationService.checkValidation(request);
        categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(String.format("Not found category by id: %s", id)));
        Category category = new Category();
        category.setName(request.getName());
        category.setId(id);
        categoryRepository.save(category);
    }

    public void deleteById(Long id){
        categoryRepository.deleteById(id);
    }
}
