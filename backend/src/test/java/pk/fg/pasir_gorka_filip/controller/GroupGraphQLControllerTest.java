package pk.fg.pasir_gorka_filip.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import pk.fg.pasir_gorka_filip.service.GroupService;

@GraphQlTest(GroupGraphQLController.class)
class GroupGraphQLControllerTest {

    // Wstrzykujemy specjalnego testera do zapytań GraphQL
    @Autowired
    private GraphQlTester graphQlTester;

    // Tworzymy "atrapę" serwisu, żeby test nie łączył się z prawdziwą bazą danych
    @MockBean
    private GroupService groupService;

    @Test
    void createGroup_ShouldThrowValidationError_WhenNameIsEmpty() {
        // Przygotowujemy czyste zapytanie GraphQL z pustą nazwą grupy
        String document = """
                mutation {
                    createGroup(groupDTO: { name: "" }) {
                        id
                    }
                }
                """;

        // Odpalamy zapytanie i sprawdzamy, czy aplikacja rzuci błędem walidacji
        graphQlTester.document(document)
                .execute()
                .errors()
                .expect(error -> error.getMessage().contains("Nazwa grupy nie może być pusta"))
                .verify();
    }
}