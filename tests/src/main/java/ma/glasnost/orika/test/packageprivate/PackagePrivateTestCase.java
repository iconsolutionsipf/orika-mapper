package ma.glasnost.orika.test.packageprivate;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.test.MappingUtil;
import ma.glasnost.orika.test.packageprivate.otherpackage.SomePublicDto;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackagePrivateTestCase {

    private final MapperFacade mapperFacade = getMapperFacade();

    @Test
    @Ignore
    public void testMappingPackagePrivateToPublic() throws Exception {
        SomePrivateEntity source = new SomePrivateEntity();
        source.setField("test value");

        final SomePublicDto actual = mapperFacade.map(source, SomePublicDto.class);

        assertEquals(source.getField(), actual.getField());
    }

    @Test
    @Ignore
    public void testMappingPublicToPackagePrivate() throws Exception {
        SomePublicDto source = new SomePublicDto();
        source.setField("test value");

        final SomePrivateEntity actual = mapperFacade.map(source, SomePrivateEntity.class);

        assertEquals(source.getField(), actual.getField());
    }

    @Test
    @Ignore
    public void testMappingPackagePrivateToPackagePrivate() throws Exception {
        SomePrivateEntity source = new SomePrivateEntity();
        source.setField("test value");

        final SimilarEntity actual = mapperFacade.map(source, SimilarEntity.class);

        assertEquals(source.getField(), actual.getField());
    }

    @Test
    @Ignore
    public void testGeneratedObjectFactory() throws Exception {
        SimilarEntityCustomConstructor source = new SimilarEntityCustomConstructor("test value");

        final SimilarEntityCustomConstructor actual = mapperFacade.map(source, SimilarEntityCustomConstructor.class);

        assertEquals(source.getField(), actual.getField());
    }

    @Test
    public void testMappingToNestedProtected() throws Exception {
        SomePublicDto source = new SomePublicDto();
        source.setField("test value");

        final SomeParentClass.SomeProtectedClass actual = mapperFacade.map(source, SomeParentClass.SomeProtectedClass.class);

        assertEquals(source.getField(), actual.getField());
    }

    @Test
    public void testMappingFromNestedProtected() throws Exception {
        SomeParentClass.SomeProtectedClass source = new SomeParentClass.SomeProtectedClass();
        source.setField("test value");

        final SomePublicDto actual = mapperFacade.map(source, SomePublicDto.class);

        assertEquals(source.getField(), actual.getField());
    }

    @Test
    @Ignore
    public void testPackagePrivateNestedEntities() throws Exception {
        NestedEntity source = new NestedEntity();
        source.setField("test value");
        
        final NestedEntity actual = mapperFacade.map(source, NestedEntity.class);
        
        assertEquals(source.getField(), actual.getField());
    }
    
    static class NestedEntity {
        private String field;
        
        public String getField() {
            return field;
        }
        
        public void setField(String field) {
            this.field = field;
        }
    }
    
    private MapperFacade getMapperFacade() {
        final MapperFactory mapperFactory = MappingUtil.getMapperFactory(true);
        mapperFactory.classMap(SomePrivateEntity.class, SomePublicDto.class);
        mapperFactory.classMap(SomePrivateEntity.class, SimilarEntity.class);
        mapperFactory.classMap(SomeParentClass.SomeProtectedClass.class, SomePublicDto.class);
        return mapperFactory.getMapperFacade();
    }
}
