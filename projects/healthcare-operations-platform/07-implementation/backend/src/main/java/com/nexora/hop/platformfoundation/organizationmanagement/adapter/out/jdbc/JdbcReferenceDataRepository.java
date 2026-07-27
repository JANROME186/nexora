package com.nexora.hop.platformfoundation.organizationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.organizationmanagement.domain.Country;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.CurrencyOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.LocaleOption;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.ReferenceDataRepository;

@Repository
@Profile("local")
class JdbcReferenceDataRepository implements ReferenceDataRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcReferenceDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Country> findAllCountries() {
        return jdbcTemplate.query("""
                select country_code, name_es_mx, name_en_us, status
                from organization.countries
                order by country_code
                """, JdbcReferenceDataRepository::mapCountry);
    }

    @Override
    public List<LocaleOption> findAllLocales() {
        return jdbcTemplate.query("""
                select locale_code, name_es_mx, name_en_us, is_default
                from organization.locales
                order by locale_code
                """, JdbcReferenceDataRepository::mapLocale);
    }

    @Override
    public List<CurrencyOption> findAllCurrencies() {
        return jdbcTemplate.query("""
                select currency_code, name_es_mx, name_en_us, minor_unit_digits
                from organization.currencies
                order by currency_code
                """, JdbcReferenceDataRepository::mapCurrency);
    }

    private static Country mapCountry(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Country(
                resultSet.getString("country_code"),
                resultSet.getString("name_es_mx"),
                resultSet.getString("name_en_us"),
                resultSet.getString("status"));
    }

    private static LocaleOption mapLocale(ResultSet resultSet, int rowNumber) throws SQLException {
        return new LocaleOption(
                resultSet.getString("locale_code"),
                resultSet.getString("name_es_mx"),
                resultSet.getString("name_en_us"),
                resultSet.getBoolean("is_default"));
    }

    private static CurrencyOption mapCurrency(ResultSet resultSet, int rowNumber) throws SQLException {
        return new CurrencyOption(
                resultSet.getString("currency_code"),
                resultSet.getString("name_es_mx"),
                resultSet.getString("name_en_us"),
                resultSet.getInt("minor_unit_digits"));
    }
}
