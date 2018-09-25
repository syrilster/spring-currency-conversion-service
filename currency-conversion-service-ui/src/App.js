import React, { Component } from "react";
import axios from "axios";
import "./App.css";
import Button from "@material-ui/core/Button";
import PropTypes from "prop-types";
import classNames from "classnames";
import { withStyles } from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
import TextField from "@material-ui/core/TextField";

const styles = theme => ({
  container: {
    display: "flex",
    flexWrap: "wrap"
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200
  },
  dense: {
    marginTop: 19
  },
  menu: {
    width: 200
  }
});

const currencies = [
  {
    value: "USD",
    label: "$"
  },
  {
    value: "EUR",
    label: "€"
  },
  {
    value: "AUD",
    label: "A$"
  }
];

class App extends Component {
  state = {
    fromCurrency: "USD",
    toCurrency: "INR",
    quantity: 0,
    exchangeRate: 0,
    calculatedAmount: 0,
    error: false
  };

  componentDidMount() {
    this.getExchangeValue();
  }

  getExchangeValue = () => {
    axios
      .get(
        "http://localhost:8765/currency-exchange/from/" +
          this.state.fromCurrency +
          "/to/" +
          this.state.toCurrency
      )
      .then(response => {
        this.setState({
          exchangeRate: response.data.conversionMultiple
        });
      });
  };

  getCalculatedAmount = () => {
    if (this.state.quantity > 0) {
      axios
        .get(
          "http://localhost:8765/currency-converter/from/" +
            this.state.fromCurrency +
            "/to/" +
            this.state.toCurrency +
            "/quantity/" +
            this.state.quantity
        )
        .then(response => {
          this.setState({
            calculatedAmount: response.data.calculatedAmount,
            error: false
          });
        });
    } else {
      this.setState({
        error: true
      });
    }
  };

  currencyFromChange = event => {
    this.setState(
      {
        fromCurrency: event.target.value
      },
      () => {
        this.getExchangeValue();
      }
    );
  };

  render() {
    const { classes } = this.props;
    const required = true;
    const marignStyle = {
      marginRight: "50px"
    };

    return (
      <div className="App">
        <h2>Currency Conversion Service</h2>

        <TextField
          id="standard-select-currency"
          select
          label="Select"
          className={classes.textField}
          value={this.state.fromCurrency}
          onChange={event => this.currencyFromChange(event)}
          SelectProps={{
            MenuProps: {
              className: classes.menu
            }
          }}
          helperText="Please select from currency"
          margin="normal"
        >
          {currencies.map(option => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </TextField>

        <br />

        <TextField
          id="standard-select-currency"
          select
          label="Select"
          className={classes.textField}
          value={this.state.toCurrency}
          onChange={event => this.setState({ toCurrency: event.target.value })}
          SelectProps={{
            MenuProps: {
              className: classes.menu
            }
          }}
          helperText="Please select to currency"
          margin="normal"
        >
          <MenuItem key="INR" value="INR">
            INR
          </MenuItem>
        </TextField>

        <br />
        <p>
          1 {this.state.fromCurrency} is equal to {this.state.exchangeRate}{" "}
          {this.state.toCurrency}
        </p>
        <br />

        <TextField
          required={required}
          error={this.state.error}
          label="Required"
          id="standard-required"
          defaultValue={this.state.quantity}
          onChange={event => {
            this.setState({ quantity: event.target.value });
          }}
          className={classes.textField}
          margin="normal"
        />
        <br />

        <Button
          onClick={this.getCalculatedAmount}
          variant="outlined"
          color="primary"
        >
          Calculate
        </Button>
        <br />
        <p>The calculated amount is {this.state.calculatedAmount}</p>
      </div>
    );
  }
}

App.propTypes = {
  classes: PropTypes.object.isRequired
};

export default withStyles(styles)(App);
