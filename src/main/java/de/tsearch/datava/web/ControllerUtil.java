package de.tsearch.datava.web;

import com.sun.istack.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ControllerUtil {

    private Sort setOrder(Sort sort, String order) throws SortException {
        if ("asc".equalsIgnoreCase(order)) {
            return sort.ascending();
        } else if ("desc".equalsIgnoreCase(order)) {
            return sort.descending();
        } else {
            throw new SortException("Sort order must be asc or desc");
        }
    }

    public PageRequest buildPageRequest(int pageNumber, int pageSize, @NotNull String sortProperty, @NotNull String sortOrder) {
        Sort sort;
        try {
            switch (sortProperty) {
                case "date":
                    sort = this.setOrder(Sort.by("createdAt"), sortOrder);
                    break;
                case "views":
                    sort = this.setOrder(Sort.by("viewCount"), sortOrder).and(Sort.by("createdAt").descending());
                    break;
                default:
                    return null;
            }
        } catch (SortException e) {
            return null;
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private static class SortException extends Exception {
        public SortException(String message) {
            super(message);
        }

        public SortException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
