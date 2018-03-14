package com.baibei.accountservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TDailyPaycenterResultExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TDailyPaycenterResultExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIdEqualTo(String value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotEqualTo(String value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThan(String value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(String value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThan(String value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(String value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLike(String value) {
            addCriterion("order_id like", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotLike(String value) {
            addCriterion("order_id not like", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIn(List<String> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotIn(List<String> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdBetween(String value1, String value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotBetween(String value1, String value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andAmountExpectIsNull() {
            addCriterion("amount_expect is null");
            return (Criteria) this;
        }

        public Criteria andAmountExpectIsNotNull() {
            addCriterion("amount_expect is not null");
            return (Criteria) this;
        }

        public Criteria andAmountExpectEqualTo(Long value) {
            addCriterion("amount_expect =", value, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectNotEqualTo(Long value) {
            addCriterion("amount_expect <>", value, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectGreaterThan(Long value) {
            addCriterion("amount_expect >", value, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectGreaterThanOrEqualTo(Long value) {
            addCriterion("amount_expect >=", value, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectLessThan(Long value) {
            addCriterion("amount_expect <", value, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectLessThanOrEqualTo(Long value) {
            addCriterion("amount_expect <=", value, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectIn(List<Long> values) {
            addCriterion("amount_expect in", values, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectNotIn(List<Long> values) {
            addCriterion("amount_expect not in", values, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectBetween(Long value1, Long value2) {
            addCriterion("amount_expect between", value1, value2, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountExpectNotBetween(Long value1, Long value2) {
            addCriterion("amount_expect not between", value1, value2, "amountExpect");
            return (Criteria) this;
        }

        public Criteria andAmountActualIsNull() {
            addCriterion("amount_actual is null");
            return (Criteria) this;
        }

        public Criteria andAmountActualIsNotNull() {
            addCriterion("amount_actual is not null");
            return (Criteria) this;
        }

        public Criteria andAmountActualEqualTo(Long value) {
            addCriterion("amount_actual =", value, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualNotEqualTo(Long value) {
            addCriterion("amount_actual <>", value, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualGreaterThan(Long value) {
            addCriterion("amount_actual >", value, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualGreaterThanOrEqualTo(Long value) {
            addCriterion("amount_actual >=", value, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualLessThan(Long value) {
            addCriterion("amount_actual <", value, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualLessThanOrEqualTo(Long value) {
            addCriterion("amount_actual <=", value, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualIn(List<Long> values) {
            addCriterion("amount_actual in", values, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualNotIn(List<Long> values) {
            addCriterion("amount_actual not in", values, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualBetween(Long value1, Long value2) {
            addCriterion("amount_actual between", value1, value2, "amountActual");
            return (Criteria) this;
        }

        public Criteria andAmountActualNotBetween(Long value1, Long value2) {
            addCriterion("amount_actual not between", value1, value2, "amountActual");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIsNull() {
            addCriterion("order_type is null");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIsNotNull() {
            addCriterion("order_type is not null");
            return (Criteria) this;
        }

        public Criteria andOrderTypeEqualTo(String value) {
            addCriterion("order_type =", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotEqualTo(String value) {
            addCriterion("order_type <>", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeGreaterThan(String value) {
            addCriterion("order_type >", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeGreaterThanOrEqualTo(String value) {
            addCriterion("order_type >=", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLessThan(String value) {
            addCriterion("order_type <", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLessThanOrEqualTo(String value) {
            addCriterion("order_type <=", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeLike(String value) {
            addCriterion("order_type like", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotLike(String value) {
            addCriterion("order_type not like", value, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeIn(List<String> values) {
            addCriterion("order_type in", values, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotIn(List<String> values) {
            addCriterion("order_type not in", values, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeBetween(String value1, String value2) {
            addCriterion("order_type between", value1, value2, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderTypeNotBetween(String value1, String value2) {
            addCriterion("order_type not between", value1, value2, "orderType");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectIsNull() {
            addCriterion("order_status_expect is null");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectIsNotNull() {
            addCriterion("order_status_expect is not null");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectEqualTo(String value) {
            addCriterion("order_status_expect =", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectNotEqualTo(String value) {
            addCriterion("order_status_expect <>", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectGreaterThan(String value) {
            addCriterion("order_status_expect >", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectGreaterThanOrEqualTo(String value) {
            addCriterion("order_status_expect >=", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectLessThan(String value) {
            addCriterion("order_status_expect <", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectLessThanOrEqualTo(String value) {
            addCriterion("order_status_expect <=", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectLike(String value) {
            addCriterion("order_status_expect like", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectNotLike(String value) {
            addCriterion("order_status_expect not like", value, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectIn(List<String> values) {
            addCriterion("order_status_expect in", values, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectNotIn(List<String> values) {
            addCriterion("order_status_expect not in", values, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectBetween(String value1, String value2) {
            addCriterion("order_status_expect between", value1, value2, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusExpectNotBetween(String value1, String value2) {
            addCriterion("order_status_expect not between", value1, value2, "orderStatusExpect");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualIsNull() {
            addCriterion("order_status_actual is null");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualIsNotNull() {
            addCriterion("order_status_actual is not null");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualEqualTo(String value) {
            addCriterion("order_status_actual =", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualNotEqualTo(String value) {
            addCriterion("order_status_actual <>", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualGreaterThan(String value) {
            addCriterion("order_status_actual >", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualGreaterThanOrEqualTo(String value) {
            addCriterion("order_status_actual >=", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualLessThan(String value) {
            addCriterion("order_status_actual <", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualLessThanOrEqualTo(String value) {
            addCriterion("order_status_actual <=", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualLike(String value) {
            addCriterion("order_status_actual like", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualNotLike(String value) {
            addCriterion("order_status_actual not like", value, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualIn(List<String> values) {
            addCriterion("order_status_actual in", values, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualNotIn(List<String> values) {
            addCriterion("order_status_actual not in", values, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualBetween(String value1, String value2) {
            addCriterion("order_status_actual between", value1, value2, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andOrderStatusActualNotBetween(String value1, String value2) {
            addCriterion("order_status_actual not between", value1, value2, "orderStatusActual");
            return (Criteria) this;
        }

        public Criteria andDealDateIsNull() {
            addCriterion("deal_date is null");
            return (Criteria) this;
        }

        public Criteria andDealDateIsNotNull() {
            addCriterion("deal_date is not null");
            return (Criteria) this;
        }

        public Criteria andDealDateEqualTo(Date value) {
            addCriterionForJDBCDate("deal_date =", value, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("deal_date <>", value, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateGreaterThan(Date value) {
            addCriterionForJDBCDate("deal_date >", value, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("deal_date >=", value, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateLessThan(Date value) {
            addCriterionForJDBCDate("deal_date <", value, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("deal_date <=", value, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateIn(List<Date> values) {
            addCriterionForJDBCDate("deal_date in", values, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("deal_date not in", values, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("deal_date between", value1, value2, "dealDate");
            return (Criteria) this;
        }

        public Criteria andDealDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("deal_date not between", value1, value2, "dealDate");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andMemoIsNull() {
            addCriterion("memo is null");
            return (Criteria) this;
        }

        public Criteria andMemoIsNotNull() {
            addCriterion("memo is not null");
            return (Criteria) this;
        }

        public Criteria andMemoEqualTo(String value) {
            addCriterion("memo =", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoNotEqualTo(String value) {
            addCriterion("memo <>", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoGreaterThan(String value) {
            addCriterion("memo >", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoGreaterThanOrEqualTo(String value) {
            addCriterion("memo >=", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoLessThan(String value) {
            addCriterion("memo <", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoLessThanOrEqualTo(String value) {
            addCriterion("memo <=", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoLike(String value) {
            addCriterion("memo like", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoNotLike(String value) {
            addCriterion("memo not like", value, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoIn(List<String> values) {
            addCriterion("memo in", values, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoNotIn(List<String> values) {
            addCriterion("memo not in", values, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoBetween(String value1, String value2) {
            addCriterion("memo between", value1, value2, "memo");
            return (Criteria) this;
        }

        public Criteria andMemoNotBetween(String value1, String value2) {
            addCriterion("memo not between", value1, value2, "memo");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}