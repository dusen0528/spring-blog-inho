package com.nhnacademy.blog.category.util;

import com.nhnacademy.blog.category.dto.CategoryResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public final class CategoryUtils {
    private CategoryUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static List<CategoryResponse> generateCategoryHierarchy(List<CategoryResponse> categoryResponseList){
        List<CategoryResponse> targetList = new ArrayList<>();

        Collections.sort(categoryResponseList, (o1,o2) ->{
            return o1.getCategorySec() - o2.getCategorySec();
        });

        for(CategoryResponse categoryResponse : categoryResponseList){
            if(Objects.isNull(categoryResponse.getCategoryPid())){
                targetList.add(categoryResponse);
                log.debug("root : {}", categoryResponse);
            }
        }

        categoryResponseList.removeIf(o->Objects.isNull(o.getCategoryPid()));

        findCategory(targetList, categoryResponseList);

        return targetList;
    };

    private static void findCategory(List<CategoryResponse> targetList, List<CategoryResponse> categoryResponseList){

        for(CategoryResponse target : targetList){
            List<CategoryResponse> filteredList = categoryResponseList.stream()
                    .filter(o->Objects.nonNull(o.getCategoryPid()))
                    .filter(o -> o.getCategoryPid().equals(target.getCategoryId()))
                    .collect(Collectors.toList());

            //subCategory 등록
            for (CategoryResponse categoryResponse : filteredList) {
                target.addSubCategory(categoryResponse);
            }

            if(target.getSubCategories().size()>0){
                findCategory(target.getSubCategories(), categoryResponseList);
            }

        }//end for
    }
}
