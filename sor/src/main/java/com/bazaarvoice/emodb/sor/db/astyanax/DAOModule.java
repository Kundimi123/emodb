package com.bazaarvoice.emodb.sor.db.astyanax;

import com.bazaarvoice.emodb.sor.DataStoreConfiguration;
import com.bazaarvoice.emodb.sor.db.*;
import com.bazaarvoice.emodb.sor.db.cql.CqlReaderDAODelegate;
import com.bazaarvoice.emodb.sor.db.cql.CqlWriterDAODelegate;
import com.bazaarvoice.emodb.table.db.astyanax.DataCopyDAO;
import com.bazaarvoice.emodb.table.db.astyanax.DataPurgeDAO;
import com.bazaarvoice.emodb.table.db.eventregistry.StorageReaderDAO;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Guice module for DAO implementations.  Separate from {@link com.bazaarvoice.emodb.sor.DataStoreModule} to allow
 * private bindings only used by the DAOs.  Required bindings are documented in DataStoreModule.
 *
 * @see com.bazaarvoice.emodb.sor.DataStoreModule
 */
public class DAOModule extends PrivateModule {

    private static final int DELTA_PREFIX_LENGTH = 4;

    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(PrefixLength.class).toInstance(DELTA_PREFIX_LENGTH);
        bind(DAOUtils.class).asEagerSingleton();
        bind(DataWriterDAO.class).annotatedWith(CqlWriterDAODelegate.class).to(AstyanaxDataWriterDAO.class);
        bind(DataWriterDAO.class).annotatedWith(AstyanaxWriterDAODelegate.class).to(CqlDataWriterDAO.class);
        bind(DataWriterDAO.class).to(CqlDataWriterDAO.class);
        bind(DataPurgeDAO.class).to(AstyanaxDataWriterDAO.class);
        bind(DataCopyDAO.class).to(DefaultDataCopyDAO.class);
        bind(DataCopyWriterDAO.class).to(CqlDataWriterDAO.class);

        bind(DataReaderDAO.class).to(CqlBlockedDataReaderDAO.class);
        bind(DataReaderDAO.class).annotatedWith(CqlReaderDAODelegate.class).to(AstyanaxBlockedDataReaderDAO.class);
        bind(DataCopyReaderDAO.class).to(AstyanaxBlockedDataReaderDAO.class);
        bind(AstyanaxKeyScanner.class).to(AstyanaxBlockedDataReaderDAO.class);

        // Explicit bindings so objects don't get created as a just-in-time binding in the root injector.
        // This needs to be done for just about anything that has only public dependencies.
        bind(AstyanaxDataWriterDAO.class).asEagerSingleton();
        bind(CqlDataWriterDAO.class).asEagerSingleton();
        bind(DefaultDataCopyDAO.class).asEagerSingleton();

        bind(AstyanaxBlockedDataReaderDAO.class).asEagerSingleton();
        bind(CqlBlockedDataReaderDAO.class).asEagerSingleton();

        bind(StorageReaderDAO.class).to(CqlBlockedDataReaderDAO.class);

        expose(DataReaderDAO.class);
        expose(DataWriterDAO.class);
        expose(DataCopyDAO.class);
        expose(DataPurgeDAO.class);
        expose(StorageReaderDAO.class);
    }

    @Provides
    @Singleton
    ChangeEncoder provideChangeEncoder(DataStoreConfiguration configuration) {
        return new DefaultChangeEncoder(configuration.getDeltaEncodingVersion());
    }

    @Provides
    @Singleton
    @BlockSize
    int provideBlockSize(DataStoreConfiguration configuration) {
        return configuration.getDeltaBlockSizeInKb() * 1024;
    }

    @Provides
    @Singleton
    @CellTombstoneBlockLimit
    int provideCellTombstoneBlockLimit(DataStoreConfiguration configuration) {
        return configuration.getCellTombstoneBlockLimit();
    }

    @Provides
    @Singleton
    @CellTombstoneCompactionEnabled
    boolean provideCellTombstoneCompactionEnabled(DataStoreConfiguration configuration) {
        return configuration.isCellTombstoneCompactionEnabled();
    }
}
